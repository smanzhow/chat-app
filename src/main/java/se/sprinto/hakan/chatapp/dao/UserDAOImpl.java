package se.sprinto.hakan.chatapp.dao;


import se.sprinto.hakan.chatapp.model.User;

import util.DatabaseUtil;


import java.sql.*;


public class UserDAOImpl implements UserDAO {


    @Override
    public User insertUser(User user) {

        String sql = "INSERT INTO chat_app_database.user (username, password) VALUES(?,?) ";

        try (Connection connection = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Antal rader uppdaterade " + rowsInserted);
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int insertedId = rs.getInt(1);

                        return new User(insertedId, user.getUsername(), user.getPassword());
                    }
                }
            } else {
                System.out.println("Fel vid uppdatering av rader.");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findUserByNamePassword(String username, String password) {

        String sql = "SELECT * FROM chat_app_database.user WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"));
                    return user;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

}
