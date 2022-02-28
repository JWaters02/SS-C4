package ss.shell.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author pedro
 */
public class ShellServer extends Thread {
    private static final int PORT = 2222;

    public static void main(String[] args) {
        try {
            ShellServer server = new ShellServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
//        try {
//            // Create a server socket
//            ServerSocket serverSocket = new ServerSocket(PORT);
//            System.out.println("Listening on port " + PORT);
//
//            // Listen for connections
//            while (true) {
//                Socket socket = serverSocket.accept();
//                System.out.println("Accepted connection from " + socket.getInetAddress());
//
//                // Create a new thread for the connection
//                new Thread(String.valueOf(new ShellServerHandler(socket))).start();
//            }
//        } catch (IOException e) {
//            System.out.println("Server exception: " + e.getMessage());
//            e.printStackTrace();
//        }
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Echo Server listening port: " + PORT);
            boolean shudown = true;
            while (shudown) {
                Socket socket = server.accept();
                InputStream is = socket.getInputStream();
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line;
                line = in.readLine();
                String auxLine = line;
                // looks for post data
                int postDataI = -1;
                while ((line = in.readLine()) != null && (line.length() != 0)) {
                    System.out.println(line);
                    if (line.contains("Content-Length:")) {
                        postDataI = Integer.parseInt(line
                                .substring(
                                        line.indexOf("Content-Length:") + 16
                                ));
                    }
                }
                StringBuilder postData = new StringBuilder();
                for (int i = 0; i < postDataI; i++) {
                    int intParser = in.read();
                    postData.append((char) intParser);
                }
                // replace + by " "
                int index= postData.toString().indexOf('+');
                while(index>-1){

                    postData = new StringBuilder(postData.substring(0, index) + ' ' + postData.substring(index + 1));
                    index= postData.toString().indexOf('+');
                }
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Server: MINISERVER");
                // this blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.println("<H1>Systems Software - Echo server</H1>");
                out.println("<H2>Example of a echo server</H2>");
                out.println("<p style=\"margin-left:5px\">GET->" + auxLine + "</H1>");
                out.println("<p style=\"margin-left:5px\" >Post->" + postData + "</p>");
                out.println("<form name=\"input\" action=\"imback\" method=\"post\">");
                out.println("Input: <input type=\"text\" name=\"user\"><input type=\"submit\" value=\"Submit\"></form>");
                //if your get parameter contains shutdown it will shutdown
                if (auxLine.contains("?shutdown")) {
                    shudown = false;
                }
                out.close();
                socket.close();
            }
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
