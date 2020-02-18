package ru.tornchat.chat.network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketThreadListener {
    void onStartServerSocketThread(ServerSocketThread thread);
    void onStopServerSocketThread(ServerSocketThread thread);

    void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket);
    void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket);

    void onSocketAccepted(ServerSocketThread thread, Socket socket);

    void onServerSocketException(ServerSocketThread thread, Exception e);
}
