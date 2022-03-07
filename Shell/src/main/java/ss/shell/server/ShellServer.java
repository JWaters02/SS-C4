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
    private Thread thread;
    private int PORT = 2222;
    private final String threadName;
    private static final int numServersToStart = 3;

    public ShellServer(String name, int port) {
        this.PORT = port;
        this.threadName = name;
    }

    public static void main(String[] args) {
        try {
            for (int i = 0; i < numServersToStart; i++) {
                new ShellServer("ShellServer" + i, 2223 + i).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Run start on each new thread
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the server.
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server " + threadName + " started on port " + PORT);

            boolean shutdown = false;
            boolean hasLoaded = false;
            boolean isLoggedIn = false;
            Shell shell;
            String username = "guest";
            String cwd = BuiltIns.HOME_PATH;
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
                    System.out.println(line);
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
                userInputSanitised.replace(0, 5, "");
                System.out.println("post data:" + userInputSanitised);
                int index = userInputSanitised.toString().indexOf('+');
                while(index > -1){
                    userInputSanitised = new StringBuilder(userInputSanitised.substring(0, index) + ' ' +
                            userInputSanitised.substring(index + 1));
                    index = userInputSanitised.toString().indexOf('+');
                }

                // If it's the first time loading the shell, only output the prompt
                if (!hasLoaded) {
                    output = "<div class=\"terminal_prompt\">\n" +
                            "<span class=\"terminal_prompt-path\">" + cwd + "</span>\n" +
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
                }

                // Output the top html
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html; charset=utf-8");
                out.println("Server: SS_SERVER");
                // this blank line signals the end of the headers
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

                // Open CSS.txt file and read each line into the PrintWriter out
                try {
                    File file = new File("src/main/java/ss/shell/server/CSS.txt");
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
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}