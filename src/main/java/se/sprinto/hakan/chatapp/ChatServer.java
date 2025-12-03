package se.sprinto.hakan.chatapp;

import se.sprinto.hakan.chatapp.service.Login;
import se.sprinto.hakan.chatapp.service.MessageService;
import se.sprinto.hakan.chatapp.dao.MessageDAO;
import se.sprinto.hakan.chatapp.dao.MessageDAOImpl;
import se.sprinto.hakan.chatapp.dao.UserDAO;
import se.sprinto.hakan.chatapp.dao.UserDAOImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private final UserDAO userDAO = new UserDAOImpl();
    private final MessageDAO messageDAO = new MessageDAOImpl();
    private final Login login = new Login(userDAO);
    private final MessageService messageService = new MessageService(messageDAO);

    private final int port;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server startar på port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this, login, messageService);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void broadcast(String message, ClientHandler sender) {
        System.out.println("Meddelande från " + sender.getUser().getUsername() + ": " + message);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(sender.getUser().getUsername() + ": " + message);
            }
        }
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println(client.getUser().getUsername() + " kopplade från.");
    }


}

