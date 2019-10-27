package com.alvindizon.tcpclientserver.data;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.content.Context.WIFI_SERVICE;

@Singleton
public class TcpRepository {

    private final TcpHelper tcpHelper;
    private final Context context;
    private PublishSubject<String> receivedMessage;

    @Inject
    public TcpRepository(TcpHelper tcpHelper, Context context) {
        this.tcpHelper = tcpHelper;
        this.context = context;
        receivedMessage = PublishSubject.create();
    }

    public Completable startTcpListen(int port) {
        return tcpHelper.init(port)
            .andThen(Observable.interval(1000, TimeUnit.MILLISECONDS).flatMap(time -> tcpHelper.receive()))
            .flatMapCompletable(message -> {
                receivedMessage.onNext(message);
                return Completable.complete();
            })
            .retryWhen(errors -> errors.flatMap(error -> Flowable.just(this))); // retry listening for new connections on disconnect,
    }


    public Completable stopTcpListen() {
        return tcpHelper.stopListening();
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(
                ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array())
                .getHostAddress();
    }

    public String getLocalWifiIpAddress() {
        String ipAddress = "N/A";
        try {
            ipAddress = getLocalIpAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    public Observable<String> getReceivedTcpMsg() {
        return receivedMessage;
    }

    public Completable sendTcpMessage(String message) {
        return tcpHelper.sendMessage(message);
    }
}
