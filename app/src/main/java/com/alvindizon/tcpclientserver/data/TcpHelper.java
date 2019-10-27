package com.alvindizon.tcpclientserver.data;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static android.content.Context.WIFI_SERVICE;

@Singleton
public class TcpHelper {
    private static final String TAG = TcpHelper.class.getSimpleName();

    private InetAddress inetAddress;
    private Socket clientSocket;
    private ServerSocket serverSocket;

    private PrintWriter output;
    private BufferedReader input;



    @Inject
    public TcpHelper() {
    }

    public Completable init( int port) {
        return Completable.create(emitter -> {
            try {
//                inetAddress = InetAddress.getByName(ipAddr);
                // init socket for listening purposes
                if(serverSocket == null) {
                    serverSocket = new ServerSocket(port);
                }
                clientSocket = serverSocket.accept();

                output = new PrintWriter(clientSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Log.d(TAG, "init() success");

            } catch (Exception e) {
                Log.e(TAG, "init exception: ",  e);
                emitter.tryOnError(e);
            }
            emitter.onComplete();
        });
    }
    // BufferedReader.readLine() will only terminate if it receives a newline
    public Observable<String> receive() {
        return Observable.create(emitter -> {
            try {
                Log.d(TAG, "receive() start");
                String message = input.readLine();
                if(!TextUtils.isEmpty(message)) {
                    emitter.onNext(message);
                } else {
                    Log.d(TAG, "disconnected");
                    emitter.tryOnError(new TcpDisconnectedException());
                }
            } catch (Exception e) {
                e.printStackTrace();
                emitter.tryOnError(e);
            }
        });
    }

    public Completable sendMessage(String message) {
        return Completable.create(emitter -> {
            try {
                output.println(message);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.tryOnError(e);
            }
        });
    }

    public Completable stopListening() {
        return Completable.create(emitter -> {
            try {
                serverSocket.close();
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.tryOnError(e);
            }
        });
    }

}
