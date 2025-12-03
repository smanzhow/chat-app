package se.sprinto.hakan.chatapp;

import se.sprinto.hakan.chatapp.service.MessageService;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;
import se.sprinto.hakan.chatapp.service.Login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private final Login login;
    private final MessageService messageService;


    private PrintWriter out;
    private User user;

    ClientHandler(Socket socket, ChatServer server, Login login, MessageService messageService) {
        this.socket = socket;
        this.server = server;
        this.login = login;
        this.messageService = messageService;

    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)

        ) {
            this.out = writer;
            writer.println("Välkommen!");

            while (user == null) {
                writer.println("Har du redan ett konto? (ja/nej)");
                String answer = in.readLine();

                if (answer == null || answer.equalsIgnoreCase("/quit")) {
                    return;
                }

                if ("ja".equalsIgnoreCase(answer)) {
                    writer.println("Ange användarnamn:");
                    String username = in.readLine();
                    writer.println("Ange lösenord:");
                    String password = in.readLine();


                    user = login.login(username, password);

                    if (user == null) {
                        writer.println("Fel användarnamn eller lösenord. Försök igen.");
                    }
                } else {
                    writer.println("Skapa nytt konto. Ange användarnamn:");
                    String username = in.readLine();
                    writer.println("Ange lösenord:");
                    String password = in.readLine();
                    user = login.registerUser(new User(username, password));
                    if (user != null) {
                        writer.println("Konto skapat. Välkommen, " + user.getUsername() + "!");
                    } else {
                        writer.println("Kunde inte skapa konto, försök igen.");

            }
        }
    }
                writer.println("Du är inloggad som: " + user.getUsername() + "");
                List<Message> messagesAndUser = messageService.leftJoinShowTextWhenLogin(user.getId());

                if (messagesAndUser.isEmpty()) {
                    out.println("Inga tidigare meddelanden.");
                } else {
                    out.println("Dina meddelanden.");
                    for (Message m : messagesAndUser) {
                        out.println(user.getUsername() + "[" + m.getTimestamp() + "]" + m.getText());
                    }
                }

                writer.println("Nu kan du börja skriva meddelanden");
                writer.println("Skriv /quit för att avsluta");
                writer.println("Skriv /mymsgs för att lista alla dina meddelanden");

            System.out.println(user.getUsername() + " anslöt.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (message.equalsIgnoreCase("/mymsgs")) {

                    List<Message> messages = messageService.getMessagesForUser(user.getId());

                    if (messages.isEmpty()) {
                        out.println("Inga sparade meddelanden.");
                    } else {
                        out.println("Dina meddelanden:");
                        for (Message m : messages) {
                            out.println("[" + m.getTimestamp() + "] " + m.getText());
                        }
                    }
                } else {
                    server.broadcast(message, this);
                    messageService.saveMessage(new Message(user.getId(), message, java.time.LocalDateTime.now()));
                }
            }

        } catch (IOException e) {
            System.out.println("Problem med klient: " + e.getMessage());
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}

