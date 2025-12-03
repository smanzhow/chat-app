package se.sprinto.hakan.chatapp.service;

import se.sprinto.hakan.chatapp.dao.MessageDAO;
import se.sprinto.hakan.chatapp.model.Message;
import java.util.List;

public class MessageService {

    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public List<Message> leftJoinShowTextWhenLogin(int id){
        return messageDAO.leftJoinShowTextWhenLogIn(id);
    }

    public List<Message> getMessagesForUser(int id){
        return messageDAO.getMessagesByUserId(id);
    }

    public Message saveMessage(Message message){
        return messageDAO.saveMessage(message);
    }

}
