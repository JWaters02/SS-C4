package ss.shell;

import ss.shell.utils.BuiltIns;
import ss.shell.utils.ConsoleColours;
import ss.shell.utils.ShellUtils;
import ss.shell.utils.Logs;

import java.util.Scanner;

public class Prompt {
    /**
     * Create the event loop that produces a prompt for user input.
     * The prompt should be the current working directory followed by a '$ '.
     */
    public static void main(String[] args) {
        BuiltInProcess bip = new BuiltInProcess();

        // Main event loop
        while (true) {
            System.out.print(prompt(bip.getUsername()));

            // Read user input
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.length() == 0) {
                Logs.printLine("No command entered.", Logs.LogLevel.ERROR);
                continue;
            }

            // Parse user input
            String[] command = input.split(" ");
            if (notShellCommand(command)) {
                continue;
            }

            // If the command is not a built-in command, run it
            if (isBuiltInCommand(command[0])) {
                bip.execute(command);
            } else {
                if (isAllowedCommand(command[0])) {
                    ShellProcess process = new ShellProcess();
                    String output = process.execute(command);
                    Logs.printLine(prompt(bip.getUsername()) + "\n" + output);
                } else {
                    Logs.printLine("Command not allowed.", Logs.LogLevel.WARNING);
                }
            }
        }
    }

    /**
     * Produce the prompt.
     * @param username The username of the currently logged-in user. If no user logged in, use 'guest'.
     * @return The prompt text.
     */
    private static String prompt(String username) {
        String cwd;
        if (username == null) {
            username = "guest";
            cwd = BuiltIns.HOME_PATH.substring(0, BuiltIns.HOME_PATH.length() - 1);
        } else {
            cwd = BuiltIns.HOME_PATH + username;
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
            Logs.printLine("No command entered.", Logs.LogLevel.ERROR);
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
