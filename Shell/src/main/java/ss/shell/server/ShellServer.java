package ss.shell.server;

import ss.shell.Prompt;
import ss.shell.utils.BuiltIns;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author pedro
 */
public class ShellServer extends Thread {
    private static final int PORT = 2222;
    private static ArrayList<String> history;

    public static void main(String[] args) {
        try {
            ShellServer server = new ShellServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Echo Server listening port: " + PORT);
            boolean shutdown = false;
            Prompt promptObj = new Prompt();
            while (!shutdown) {
                Socket socket = server.accept();
                InputStream is = socket.getInputStream();
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line;
                line = in.readLine();
                String auxLine = line;

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
                System.out.println("post data:" + userInputSanitised);
                int index = userInputSanitised.toString().indexOf('+');
                while(index > -1){
                    userInputSanitised = new StringBuilder(userInputSanitised.substring(0, index) + ' ' +
                            userInputSanitised.substring(index + 1));
                    index = userInputSanitised.toString().indexOf('+');
                }

                try {
                    // If user has inputted something, add it to the end of the html
                    if (!userInputSanitised.isEmpty()) {
                        addInputToHTML(userInputSanitised.toString());
                    }

                    // Call the appropriate Prompt function here
                    // If user input requires short next prompt
                    for (String command : BuiltIns.SHORTS) {
                        if (userInputSanitised.toString().contains(command)) {

                        };
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] prompt = promptObj.getFullPrompt();

                ArrayList<String> outputs = buildHTML(prompt, "blah", BuiltIns.PromptType.FULL);
                // Output the HTML onto the page
                for (String output : outputs) {
                    out.println(output);
                }


                //if your get parameter contains shutdown it will shutdown
                if (auxLine.contains("?shutdown")) {
                    shutdown = true;
                }
                out.close();
                socket.close();
            }
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> buildHTML(String[] prompt, String output,
                                              BuiltIns.PromptType promptType) {
        // Top is HTTP header, title, input form
        ArrayList<String> top = new ArrayList<String>();
        top.add("HTTP/1.0 200 OK");
        top.add("Content-Type: text/html; charset=utf-8");
        top.add("Server: MINISERVER");
        top.add("");
        top.add("<h1>Systems Software - Remote Shell</h1>");
        top.add("<form name=\"input\" action=\"imback\" method=\"post\">");
        top.add("<span style=\"color:black\">Input:</span>");
        top.add("<input type=\"text\" name=\"user\">");
        top.add("<input type=\"submit\" value=\"Submit\">");
        top.add("</form>");
        top.add("<p>");

        // Out is the history plus the new stuff
        ArrayList<String> out = new ArrayList<String>(history);
        out.add("   <p></p>");
        if (promptType == BuiltIns.PromptType.FULL) {
            out.add("<span style=\"color:green;margin-left:5px\">");
            out.add(prompt[0]); // Username
            out.add(": </span>");
            out.add("<span style=\"color:blue\"></span><span style=\"color:black\">");
            out.add(prompt[1]); // Path
            out.add("</span><span style=\"color:black\">");
            out.add(prompt[2]); // Bling
            out.add("</span>");
        } else if (promptType == BuiltIns.PromptType.OUTPUT) {
            out.add("");
        } else if (promptType == BuiltIns.PromptType.SHORT) {
            out.add("");
        }

        // History is (history + out) = out
        history = out;

        // Add the closing paragraph tag
        out.add("</p>");

        // Return the top and out
        ArrayList<String> ret = new ArrayList<String>();
        ret.addAll(top);
        ret.addAll(out);
        return ret;
    }

    private static void addInputToHTML(String input) {
        ArrayList<String> out = new ArrayList<String>(history);
        out.add("<span style=\"color:black\">");
        out.add(input);
        out.add("</span>");
        history.addAll(out);
    }

    private static String buildShortPrompt(String[] promptDetails) {
        return "";
    }

    private static String buildFullPrompt(String[] promptDetails) {
        String username = promptDetails[0];
        String path = promptDetails[1];
        String promptSymbol = promptDetails[2];
        // Green for "username: "
        // Blue for "path"
        // Black for "promptSymbol"
        return "<span style=\"color:green;margin-left:5px\">" +
                username +
                ": </span>" +
                "<span style=\"color:blue\">" +
                path +
                "</span>" +
                "<span style=\"color:black\">" +
                promptSymbol +
                "</span>";
    }
}
