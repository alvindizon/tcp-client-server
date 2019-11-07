package com.alvindizon.tcpclientserver.data;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;

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

    public Completable initServer(int port) {
        return Completable.create(emitter -> {
            try {
                Log.d(TAG, "initServer: start");
//                inetAddress = InetAddress.getByName(ipAddr);
                // initServer socket for listening purposes
                if(serverSocket == null) {
                    Log.d(TAG, "initServer: serversocket null");
                    serverSocket = new ServerSocket(port);
                }
                clientSocket = serverSocket.accept();

                output = new PrintWriter(clientSocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Log.d(TAG, "initServer() success");

            } catch (Exception e) {
                Log.e(TAG, "initServer exception: ",  e);
                emitter.tryOnError(e);
            }
            emitter.onComplete();
        });
    }

    public Completable initClient(String ipAddress, int port) {
        return Completable.create(emitter -> {
            try {
                Log.d(TAG, "initClient: ");
                clientSocket = new Socket(ipAddress, port);
                output = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.tryOnError(e);
            }
        });
    }

    // BufferedReader.readLine() will only terminate if it receives a newline
    public Observable<String> receive() {
        return Observable.create(emitter -> {
            try {
                Log.d(TAG, "receive() start");
                String message = input.readLine();
                if(!TextUtils.isEmpty(message)) {
                    Log.d(TAG, "receive: " + message);
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
                serverSocket = null;
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                emitter.tryOnError(e);
            }
        });
    }

}
