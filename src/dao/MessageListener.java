package dao;

import model.Message;

public interface MessageListener {
    void onMessageReceived(Message message);
}
