package ru.tornchat.chat.server.core;

import ru.tornchat.chat.library.Messages;
import ru.tornchat.chat.network.SocketThread;
import ru.tornchat.chat.network.SocketThreadListener;

import java.net.Socket;

public class ClientThread extends SocketThread {

    public ClientThread(SocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }

    private String nickname;
    private boolean isAuthorized;
    private boolean isReconnected;

    String getNickname() {
        return nickname;
    }

    boolean isAuthorized() {
        return isAuthorized;
    }

    void authorizeAccept(String nickname) {
        isAuthorized = true;
        this.nickname = nickname;
        sendMessage(Messages.getAuthAccepted(nickname));
    }

    void authorizeError() {
        sendMessage(Messages.getAuthDenied());
        close();
    }

    void msgFormatError(String value) {
        sendMessage(Messages.getMsgFormatError(value));
        close();
    }

    public boolean isReconnected() {
        return isReconnected;
    }

    void reconnect() {
        isReconnected = true;
        close();
    }

}
