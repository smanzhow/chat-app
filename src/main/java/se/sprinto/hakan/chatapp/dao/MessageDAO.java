package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;


import java.util.List;

public interface MessageDAO {

    Message saveMessage(Message message);


    List<Message> getMessagesByUserId(int userId);

    List<Message> leftJoinShowTextWhenLogIn(int userId);
}
