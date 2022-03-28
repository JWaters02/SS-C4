package ss.shell.server;

import ss.shell.Shell;
import ss.shell.utils.BuiltIns;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author pedro
 * @author Joshua
 */
public class ShellServer extends Thread {
    private static int PORT = 2223;
    private final String threadName;
    private static final int NUM_SERVERS_TO_START = 3;

    /**
     * ShellServer constructor.
     * @param name The name of the server.
     * @param port The port to listen on.
     */
    public ShellServer(String name, int port) {
        PORT = port;
        this.threadName = name;
    }

    /**
     * Starts the servers.
     * @param args Java main args.
     */
    public static void main(String[] args) {
        try {
            for (int i = 0; i < NUM_SERVERS_TO_START; i++) {
                Thread.sleep(50L);
                System.out.println("Starting next server on port " + (PORT + i));
                new ShellServer("ShellServer " + i, PORT + i).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the server (overrides thread runnable).
     */
    public void run() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server " + threadName + " started on port " + PORT);

            boolean shutdown = false;
            boolean hasLoaded = false;
            boolean isLoggedIn = false;
            Shell shell;
            String username = "guest";
            String cwd = BuiltIns.HOME_PATH + username;
            String prompt = "/home/" + username;
            BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
            String output = "";

            while (!shutdown) {
                Socket socket = server.accept();
                InputStream is = socket.getInputStream();
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line;
                line = in.readLine();

                // looks for user input post data
                int userInput = -1;
                while ((line = in.readLine()) != null && (line.length() != 0)) {
                    if (line.contains("Content-Length:")) {
                        userInput = Integer.parseInt(line
                                .substring(line.indexOf("Content-Length:") + 16));
                    }
                }

                // Builds the correct user input string
                StringBuilder userInputSanitised = new StringBuilder();
                for (int i = 0; i < userInput; i++) {
                    int intParser = in.read();
                    userInputSanitised.append((char) intParser);
                }

                // Remove the new line chars
                userInputSanitised.replace(0, 5, "");
                // Replace any instances of "%2F" with "/"
                userInputSanitised.replace(0, userInputSanitised.length(),
                        userInputSanitised.toString().replace("%2F", "/"));
                // Replace any instances of "+" with " "
                userInputSanitised.replace(0, userInputSanitised.length(),
                        userInputSanitised.toString().replace("+", " "));
                System.out.println("post data: " + userInputSanitised);

                // If it's the first time loading the shell, only output the prompt
                if (!hasLoaded) {
                    output = "<div class=\"terminal_prompt\">\n" +
                            "<span class=\"terminal_prompt-path\">" + prompt + "</span>\n" +
                            "<span class=\"terminal_prompt-end\">:~$</span>" +
                            "\n</div>";
                } else {
                    // Get the output based on the user's command
                    shell = new Shell(userInputSanitised.toString(), username, cwd, userType, isLoggedIn);

                    // Update all the params
                    shutdown = shell.getDoExit();
                    isLoggedIn = shell.getIsLoggedIn();
                    username = shell.getUsername();
                    output = shell.getOutput();
                    userType = shell.getUserType();
                    cwd = shell.getCWD();
                    prompt = "/home/" + username;
                }

                // Output the top html
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Server: SS_SERVER");
                // This blank line signals the end of the headers
                out.println("");
                // Send the HTML page
                out.println("<body>\n" +
                        "    <main id=\"container\">\n" +
                        "        <div id=\"terminal\">\n" +
                        "            <section id=\"terminal_bar\">\n" +
                        "                <p id=\"bar_user\">" + username + "</p>\n" +
                        "                <form name=\"input\" method=\"post\">\n" +
                        "                    <input class=\"terminal_input\" type=\"text\" name=\"user\">\n" +
                        "                </form>\n" +
                        "            </section>" +
                        "            <section id=\"terminal_body\">");
                // Output the prompt or whatever else needs to be outputted from the result of the last command
                out.println(output);
                // Output the end of the html
                out.println("            </section>\n" +
                        "        </div>\n" +
                        "    </main>\n" +
                        "</body>");

                // Open CSS.css file and read each line into the PrintWriter out
                try {
                    File file = new File("src/main/java/ss/shell/server/CSS.css");
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        out.println(scanner.nextLine());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hasLoaded = true;

                // Close the print writer and the socket
                out.close();
                socket.close();
            }
            stopServer(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the server.
     * @param server The socket to close.
     */
    public void stopServer(ServerSocket server) {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}