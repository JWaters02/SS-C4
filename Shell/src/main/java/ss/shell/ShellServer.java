package ss.shell;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class ShellServer {
    /*
    Use web sockets to build a simple HTTPS server that can be used to run the shell.
    The server should be able to run the shell in a separate thread, and should be able to
    send and receive messages to and from the shell.
    */

    private static final int PORT = 9091;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        try {
            ShellServer server = new ShellServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Listening on port " + PORT);

            // Listen for connections
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection from " + socket.getInetAddress());

                // Create a new thread for the connection
                new Thread(String.valueOf(new ShellServerHandler(socket))).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
