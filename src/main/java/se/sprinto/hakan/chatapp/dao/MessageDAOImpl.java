package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;

import util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;


public class MessageDAOImpl implements MessageDAO {


    @Override
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM chat_app_database.message WHERE user_id = ?";

        try (Connection connection = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("message_id"),
                            rs.getInt("user_id"),
                            rs.getString("text"),
                            converterTimestamp(rs));

                    messages.add(message);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message saveMessage(Message message) {
        String sql = "INSERT INTO  chat_app_database.message (user_id, text, timestamp) VALUES (?,?,?)";
        try (Connection connection = DatabaseUtil.getInstance().getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            pstmt.setInt(1, message.getUserId());
            pstmt.setString(2, message.getText());
            pstmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));

            int insertedRows = pstmt.executeUpdate();
            if (insertedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int insertedId = rs.getInt(1);
                        return new Message(insertedId,
                                message.getUserId(),
                                message.getText(),
                                message.getTimestamp());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Message> leftJoinShowTextWhenLogIn(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = ("SELECT user.user_id, user.username, message.message_id, message.timestamp, message.text " +
                "FROM chat_app_database.user " +
                "LEFT JOIN chat_app_database.message " +
                "ON user.user_id = message.user_id " +
                "WHERE user.user_id = ?");

        try (Connection connection = DatabaseUtil.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {


                    int messageId = rs.getInt("message_id");
                    String userName = rs.getString("username"); // Används ej ju.
                    String text = rs.getString("text");
                    LocalDateTime timestamp = converterTimestamp(rs);

                    Message message = new Message(messageId,userId, text, timestamp);

                    messages.add(message);


                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return messages;
    }

    private LocalDateTime converterTimestamp(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("timestamp");


        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

}


