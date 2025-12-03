package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.User;


public interface UserDAO {


    User insertUser(User user);

    User findUserByNamePassword(String username, String password);



}
