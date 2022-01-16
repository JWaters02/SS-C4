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
     * E.g. /home/user/dir$
     */
    public static void main(String[] args) {
        // Main event loop
        while (true) {
            System.out.print(prompt());

            // Read user input
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            // Parse user input
            String[] command = input.split(" ");
            if (notShellCommand(command)) {
                continue;
            }

            // If the command is not a built-in command, run it
            if (isBuiltInCommand(command[0])) {
                BuiltInProcess bip = new BuiltInProcess(command);
                bip.execute();
            } else {
                ShellProcess process = new ShellProcess();
                String output = process.execute(command);
                System.out.println(prompt() + "\n" + output);
            }
        }
    }

    /**
     * Produce the prompt.
     */
    private static String prompt() {
        String username = System.getProperty("user.name"); // TODO: Change to currently logged in user
        String cwd = System.getProperty("user.dir");
        return ConsoleColours.GREEN + username + ": " + ConsoleColours.BLUE + cwd + ConsoleColours.RESET + "$ ";
    }

    /**
     * Check if the command is not a shell command, but a built-in one.
     * @param command The command to check.
     */
    private static boolean isBuiltInCommand(String command) {
        return BuiltIns.isBuiltIn(command.toLowerCase());
    }

    /**
     * If the command is not valid, print an error message.
     * If the command is an exit command, exit the shell.
     * @param command The command to check.
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
     */
    private static boolean isExit(String[] command) {
        return command[0].equals("exit") || command[0].equals("quit") || command[0].equals("bye");
    }
}
