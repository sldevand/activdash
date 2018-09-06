package fr.geringan.activdash.interfaces;

public interface SocketIOEventsListener {
    void onSocketIOConnect();

    void onSocketIOTimeout();

    void onSocketIODisconnect();

    void onSocketIOMessage(Object... args);
}