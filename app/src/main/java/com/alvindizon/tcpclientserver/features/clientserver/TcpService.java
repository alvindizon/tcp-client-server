package com.alvindizon.tcpclientserver.features.clientserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alvindizon.tcpclientserver.R;
import com.alvindizon.tcpclientserver.core.Actions;
import com.alvindizon.tcpclientserver.data.TcpRepository;
import com.alvindizon.tcpclientserver.di.Injector;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.alvindizon.tcpclientserver.core.Const.TCP_NOTIF_CHANNEL_ID;
import static com.alvindizon.tcpclientserver.core.Const.TCP_NOTIF_CHANNEL_NAME;
import static com.alvindizon.tcpclientserver.core.Const.TCP_PORT;
import static com.alvindizon.tcpclientserver.core.Const.TCP_SERVICE_ID;

public class TcpService extends Service {
    private static final String TAG = TcpService.class.getSimpleName();

    private CompositeDisposable compositeDisposable;
    private Scheduler intentScheduler;
    private boolean isServiceStarted = false;

    @Inject
    TcpRepository tcpRepository;

    @Override
    public void onCreate() {
        Injector.get().inject(this);
        super.onCreate();
        compositeDisposable = new CompositeDisposable();
        intentScheduler = AndroidSchedulers.from(Looper.myLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent != null) {
            if( intent != null) {
                if(intent.getAction().contentEquals(Actions.START.name())) {
                    Notification notification = createNotification();
                    startForeground(TCP_SERVICE_ID, notification);
                    startService();
                } else if(intent.getAction().contentEquals(Actions.STOP.name())) {
                    stopService();
                } else if(intent.getAction().contentEquals(Actions.RESTART.name())) {
                    restartService();
                } else {
                    Log.d(TAG, "onStartCommand: No action specified, this should never happen!");
                }
            } else {
                Log.d(TAG, "onStartCommand: null intent, service has probably been restarted");
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startService() {
        Log.d(TAG, "startService: starting...");
        if(isServiceStarted) {
            return;
        }
        isServiceStarted = true;
        compositeDisposable.add(tcpRepository.startTcpListen(5000)  // TODO make PORT no configurable
                .subscribeOn(Schedulers.io())
                .observeOn(intentScheduler)
                .subscribe(() -> Log.d(TAG, "startService: message received"),
                        error -> Log.e(TAG, "Tcp error: " + error.getMessage())
                )
        );
    }

    private void stopService() {
        Log.d(TAG, "stopService: stopping foreground service...");
        compositeDisposable.add(tcpRepository.stopTcpListen()
            .subscribeOn(Schedulers.io())
            .observeOn(intentScheduler)
            .subscribe(() ->{
                    isServiceStarted = false;
                    stopForeground(true);
                    stopSelf();
                }, error -> Log.d(TAG, "Error stopping service: " + error.getMessage())
            )
        );
    }

    private void restartService() {
        Log.d(TAG, "restartService: restarting foreground service...");
        compositeDisposable.add(tcpRepository.stopTcpListen()
            .andThen(Completable.defer(() -> tcpRepository.startTcpListen(TCP_PORT)))
            .subscribeOn(Schedulers.io())
            .observeOn(intentScheduler)
            .subscribe(() ->{
                Log.d(TAG, "Service restarted");
                isServiceStarted = true;
            }, error -> {
                Log.d(TAG, "Error restarting service: " + error.getMessage());
                isServiceStarted = false;
            })
        );
    }

    private Notification createNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    TCP_NOTIF_CHANNEL_ID,
                    TCP_NOTIF_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Tcp Service Notification");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,  0, new Intent(this, MainActivity.class), 0);

        Notification.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, TCP_NOTIF_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("Tcp Service")
                .setContentText("Listening at: " + tcpRepository.getLocalWifiIpAddress() + ":" + TCP_PORT)
//                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker Text")
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
    }
}
