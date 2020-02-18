package ru.tornchat.chat.network;

import java.net.Socket;

public interface SocketThreadListener {
    void onStartSocketThread(SocketThread thread, Socket socket);
    void onStopSocketThread(SocketThread thread);

    void onSocketIsReady(SocketThread thread, Socket socket);
    void onReceiveString(SocketThread thread, Socket socket, String value);

    void onSocketThreadException(SocketThread thread, Exception e);
}
