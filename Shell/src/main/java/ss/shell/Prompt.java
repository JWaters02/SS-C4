package ss.shell;

import ss.shell.utils.BuiltIns;
import ss.shell.utils.ConsoleColours;
import ss.shell.utils.ShellUtils;
import ss.shell.utils.Logs;
import ss.shell.utils.Logs.*;

import java.util.Scanner;

public class Prompt {
    private static BuiltIns.UserTypes userType;
    private static String username;
    private static String cwd;

    public Prompt() {
        username = "guest";
        userType = BuiltIns.UserTypes.STANDARD;
        cwd = BuiltIns.HOME_PATH;
    }

    /**
     * Create the event loop that produces a prompt for user input.
     * The prompt should be the current working directory followed by a '$ '.
     */
    public static void main(String[] args) {
        BuiltInProcess bip = new BuiltInProcess();

        // Main event loop
        while (true) {
            username = bip.getUsername();
            cwd = bip.getCWD();
            Logs.print(prompt(username, cwd), Store.NO);

            // Read user input
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.length() == 0) {
                Logs.printLine("No command entered.", LogLevel.ERROR);
                continue;
            }

            // Parse user input
            String[] command = input.split(" ");
            if (notShellCommand(command)) {
                continue;
            }

            // If the command is not a built-in command, run it
            if (isBuiltInCommand(command[0])) {
                Logs.logToFile(command);
                bip.execute(command);
            } else {
                if (isAllowedCommand(command[0])) {
                    Logs.logToFile(command);
                    ShellProcess process = new ShellProcess(bip.getCWD());
                    String output = process.execute(command);
                    // TODO: Might want to store output depending on if it is error or not
                    Logs.printLine(output, Store.NO);
                } else {
                    Logs.printLine("Command does not exist or is not allowed.", LogLevel.WARNING);
                }
            }
        }
    }

    public String[] getFullPrompt() {
        String[] ret = new String[3];
        ret[0] = username;
        ret[1] = cwd;
        ret[2] = "$";
        return ret;
    }

    /**
     * Produce the prompt.
     * @param username The username of the currently logged-in user. If no user logged in, use 'guest'.
     * @param cwd The current working directory.
     * @return The prompt text.
     */
    private static String prompt(String username, String cwd) {
        if (username == null) {
            username = "guest";
            cwd = BuiltIns.HOME_PATH.substring(0, BuiltIns.HOME_PATH.length() - 1);
        }
        return ConsoleColours.GREEN + username + ": " + ConsoleColours.BLUE + cwd + ConsoleColours.RESET + "$ ";
    }

    /**
     * Check if the command is not a shell command, but a built-in one.
     * @param command The command to check.
     * @return True if the command is a built-in command, false otherwise.
     */
    private static boolean isBuiltInCommand(String command) {
        return BuiltIns.isBuiltIn(command.toLowerCase());
    }

    /**
     * Check if the command is allowed to be executed with process builder.
     * @param command The command to check.
     * @return If the command is not allowed, return false.
     */
    private static boolean isAllowedCommand(String command) {
        return BuiltIns.isProcessBuilder(command.toLowerCase());
    }

    /**
     * If the command is not valid, print an error message.
     * If the command is an exit command, exit the shell.
     * @param command The command to check.
     * @return True if the command is valid or an exit, false otherwise.
     */
    private static boolean notShellCommand(String[] command) {
        if (command.length == 0) {
            Logs.printLine("No command entered.", LogLevel.ERROR);
            return true;
        }
        if (isExit(command)) {
            Logs.printLine("Exiting shell...");
            ShellUtils.wait(1);
            System.exit(0);
        }
        return false;
    }

    /**
     * Check if the command is to do with exiting the shell.
     * List of exit commands:
     * exit, quit, bye
     * @param command The command to check.
     * @return True if the command is an exit command, false otherwise.
     */
    private static boolean isExit(String[] command) {
        return command[0].equals("exit") || command[0].equals("quit") || command[0].equals("bye");
    }
}
