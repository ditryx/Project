package ru.tornchat.chat.server.core;

import ru.tornchat.chat.library.Messages;
import ru.tornchat.chat.network.ServerSocketThread;
import ru.tornchat.chat.network.ServerSocketThreadListener;
import ru.tornchat.chat.network.SocketThread;
import ru.tornchat.chat.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    ServerSocketThread serverSocketThread;
    private final ChatServerListener listener;
    private final DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss: ");

    private Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener){
        this.listener = listener;
    }

    public void start(int port) {
        if(serverSocketThread != null && serverSocketThread.isAlive())
            putLog("Server is already running;");
        else {
            serverSocketThread = new ServerSocketThread(this, "Server Thread", port, 2000);
            SqlClient.connect();
        }
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive())
            putLog("Server is not running");
        else {
            serverSocketThread.interrupt();
            SqlClient.disconnect();
            for (SocketThread thread : clients) {
                thread.close();
            }
        }
    }

    void putLog(String msg){
        msg = dateFormat.format(System.currentTimeMillis()) +
                Thread.currentThread().getName() + ": " + msg;
        listener.onChatServerLog(this, msg);
    }

    private String getUsers() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
            stringBuilder.append(client.getNickname()).append(Messages.DELIMITER);
        }
        return stringBuilder.toString();
    }

    /**
     * Методы ServerSocketThread
    **/

    @Override
    public void onStartServerSocketThread(ServerSocketThread thread) {
        putLog("Server has started");
    }

    @Override
    public void onStopServerSocketThread(ServerSocketThread thread) {
        putLog("Server has stopped");
    }

    @Override
    public void onCreateServerSocket(ServerSocketThread thread, ServerSocket serverSocket) {
        putLog("Server Socket created");
    }

    @Override
    public void onAcceptTimeout(ServerSocketThread thread, ServerSocket serverSocket) {

    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, Socket socket) {
        putLog("Client has joined" + socket);
        String threadName = "SocketThread " + socket.getInetAddress() + ": " +socket.getPort();
        new ClientThread(this, "Thread", socket);
    }

    @Override
    public void onServerSocketException(ServerSocketThread thread, Exception e) {
        putLog("Exception: " + getClass().getName() + e.getMessage());
    }

    /**
     * Методы SocketThread
     **/
    @Override
    public synchronized void onStartSocketThread(SocketThread thread, Socket socket) {
        putLog("SocketThread " + socket.getInetAddress() + ":" + socket.getPort() + " started");
    }

    @Override
    public synchronized void onStopSocketThread(SocketThread thread) {
        putLog("SocketThread stopped");
        ClientThread client = (ClientThread) thread;
        clients.remove(thread);
        if (client.isAuthorized()){
            sendToAuthorizedClients(Messages.getTypeBroadcast("Server",
                    client.getNickname() + " disconnected"));
            sendToAuthorizedClients(Messages.getUserList(getUsers()));
        }
    }

    @Override
    public synchronized void onSocketIsReady(SocketThread thread, Socket socket) {
        putLog("Socket " + socket.getInetAddress() + ":" + socket.getPort() + " is ready");
        clients.add(thread);
    }

    @Override
    public synchronized void onReceiveString(SocketThread thread, Socket socket, String value) {
            ClientThread client = (ClientThread) thread;
            if (client.isAuthorized()){
                handleAuthMessages(client, value);
            } else {
                handleNonAuthMessages(client, value);
            }

    }

    @Override
    public synchronized void onSocketThreadException(SocketThread thread, Exception e) {
        e.printStackTrace();
    }

    private ClientThread findClientByNickname(String nickname) {
        for (int i = 0; i <clients.size() ; i++) {
            ClientThread client = (ClientThread)clients.get(i);
            if (!client.isAuthorized()) continue;
            if (client.getNickname().equals(nickname))
                return client;
        }
        return null;
    }

    private void handleAuthMessages(ClientThread client, String value){
        String[] arr = value.split(Messages.DELIMITER);
        String msgType = arr[0];
        switch (msgType){
            case Messages.TYPE_RANGECAST:
                sendToAuthorizedClients(Messages.getTypeBroadcast(client.getNickname(),arr[1]));
                break;
            default:
                client.msgFormatError(value);
        }

    }

    private void handleNonAuthMessages(ClientThread newClient, String value){
        String[] arr = value.split(Messages.DELIMITER);
        if (arr.length !=3 || !arr[0].equals(Messages.AUTH_REQUEST)){
            newClient.msgFormatError(value);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);
        if (nickname == null) {
            putLog("Invalid login/password login: '" +
                    login + "' password: " + password + "'");
            newClient.authorizeError();
            return;
        }

        ClientThread client = findClientByNickname(nickname);
        newClient.authorizeAccept(nickname);
        if (client == null) {
            sendToAuthorizedClients(Messages.getTypeBroadcast("Server", nickname + " has joined"));
        } else {
            client.reconnect();
            clients.remove(client);
        }
        sendToAuthorizedClients(Messages.getUserList(getUsers()));
    }

    private void sendToAuthorizedClients(String value) {
        for (int i = 0; i <clients.size(); i++) {
            ClientThread client = (ClientThread) clients.get(i);
            if (!client.isAuthorized()) continue;
                client.sendMessage(value);
        }
    }

}
