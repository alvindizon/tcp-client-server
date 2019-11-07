package com.alvindizon.tcpclientserver.features.clientserver;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alvindizon.tcpclientserver.data.TcpDisconnectedException;
import com.alvindizon.tcpclientserver.data.TcpRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private final TcpRepository tcpRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<String> status = new MutableLiveData<>();

    @Inject
    public MainViewModel(TcpRepository tcpRepository) {
        this.tcpRepository = tcpRepository;
    }

    public LiveData<String> observeReceivedTcpMsg() {
        MutableLiveData<String> received = new MutableLiveData<>();
        compositeDisposable.add(tcpRepository.getReceivedTcpMsg()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(received::setValue,
                        Throwable::printStackTrace
                )
        );
        return received;
    }

    public void sendTcpMessage(String message) {
        compositeDisposable.add( tcpRepository.sendTcpMessage(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> Log.d(TAG, "sent message"),
                    Throwable::printStackTrace)
        );
    }

    public LiveData<String> initClient(String ipAddress, int port) {
        compositeDisposable.add(tcpRepository.initClient(ipAddress, port)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> status.setValue("Connected"),
                error -> {
                    error.printStackTrace();
                    status.setValue("Connect fail");
                }
            )
        );
        return status;
    }

    public void startClientListen() {
        compositeDisposable.add(tcpRepository.startClientListen()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> Log.d(TAG, "startClientListen: complete"),
                error -> {
                    error.printStackTrace();
                    if(error instanceof TcpDisconnectedException) {
                        status.setValue("Disconnected");
                    }
                }));
    }

}
