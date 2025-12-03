package se.sprinto.hakan.chatapp.service;

import se.sprinto.hakan.chatapp.dao.UserDAO;
import se.sprinto.hakan.chatapp.model.User;


public class Login {

    private final UserDAO userDAO;


    public Login(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password){
        return userDAO.findUserByNamePassword(username,password);
    }

    public User registerUser(User user){
        return userDAO.insertUser(user);

    }
}
