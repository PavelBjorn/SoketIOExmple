package com.sample.pavel.soketioexmple.socket;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by User on 19.09.2017.
 */

public class Client {

    private Socket socket;
    private OnMessageCallback mCallback;
    private Handler mHandler = new Handler();

    public Client(OnMessageCallback callback) {
        mCallback = callback;
    }

    public void onStop() {
        disconnect();
        mCallback = null;
    }

    private void closeSoccet() {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(final int port, final String address) {
        mCallback.onStartConnection();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (socket != null && socket.isConnected()) {
                        socket.close();
                    }

                    socket = new Socket(address, port);
                    socket.setKeepAlive(true);
                    BufferedReader dataInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    if (!socket.isClosed()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onConnected();
                            }
                        });
                    }

                    while (socket != null && !socket.isClosed()) {
                        final String message = dataInputStream.readLine();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onMessage(message);
                            }
                        });
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                    closeSocket();
                }
            }
        }).start();
    }

    public void sendMessage(Context context, final String message) {
        if (socket == null || socket.isClosed()) {
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect() {
        closeSocket();
    }

    public interface OnMessageCallback {

        void onStartConnection();

        void onConnected();

        void onStopConncetion();

        void onMessage(String message);
    }
}
