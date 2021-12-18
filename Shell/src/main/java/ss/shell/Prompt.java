package ss.shell;

import java.util.Scanner;

import ss.shell.utils.BuiltIns;
import ss.shell.utils.ConsoleColours;
import ss.shell.utils.ShellUtils;

public class Prompt {
    /**
     * Create the event loop that produces a prompt for user input.
     * The prompt should be the current working directory followed by a '$ '.
     * E.g. /home/user/dir$
     * The prompt should be printed to the console.
     * The user input should be read from the console.
     * The user input should be parsed into a command and arguments.
     * The command will be executed using ProcessBuilder.
     * The command output should be printed to the console.
     * The command error output should be printed to the console.
     * The command should be executed in the current working directory.
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

            if (isBuiltInCommand(command[0])) {
                // Execute built-in command
                BuiltInProcess bip = new BuiltInProcess(command, prompt());
                bip.execute();
            } else {
                // Execute command
                ShellProcess process = new ShellProcess();
                String output = process.execute(command);

                // Print command output
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
            System.out.println("No command entered.");
            return true;
        }
        if (isExit(command)) {
            System.out.println("Exiting shell...");
            ShellUtils.wait(1);
            System.exit(0);
        }
        return false;
    }

    /**
     * Check if the command is to do with exiting the shell.
     */
    private static boolean isExit(String[] command) {
        /**
         * List of exit commands:
         * exit, quit, bye
         */
        return command[0].equals("exit") || command[0].equals("quit") || command[0].equals("bye");
    }

}
