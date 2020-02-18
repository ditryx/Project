package ru.tornchat.chat.server.core;

public interface ChatServerListener {

    void onChatServerLog(ChatServer server, String message);

}
