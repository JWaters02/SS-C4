package ss.shell;

import ss.shell.utils.BuiltIns;

import java.util.Scanner;

public class BuiltInProcess {
    // TODO: Check if users have the permissions to run the command

    private final String[] command;
    private final String prompt;

    public BuiltInProcess(String[] command, String prompt) {
        this.command = command;
        this.prompt = prompt;
    }

    /**
     * Execute the correct command based on command
     */
    public void execute() {
        switch (command[0]) {
            case BuiltIns.SUPER:
                if (command.length == 1) {
                    System.out.println("Please specify a command.");
                    return;
                }
                switch (command[1]) {
                    case BuiltIns.ADDUSER:
                        addUser();
                        break;
                    default:
                        System.out.println("Cannot execute command as super: " + command[1]);
                        return;
                }
                break;
            case BuiltIns.HELP:
                help();
                break;
            default:
                System.out.println("Unknown command: " + command[0]);
                return;
        }
    }

    /**
     * Execute add user command.
     * command[0] is super
     * command[1] is adduser
     */
    private void addUser() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        int password1 = scanner.nextLine().hashCode(); // TODO: Mask password input
        System.out.print("Retype password: ");
        int password2 = scanner.nextLine().hashCode();
        if (password1 != password2) {
            System.out.println("Passwords do not match!");
            return;
        }

        System.out.print("Enter user type: ");
        String userType = scanner.nextLine();
        // TODO: Validate user type
        // TODO: Add user to database

        System.out.print("User: " + username + " added with success!\n");
    }

    /**
     * Execute delete user command.
     * command[0] is super
     * command[1] is deluser
     */
    private void deleteUser() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
    }

    /**
     * Execute help command.
     * command[0] is help
     */
    private void help() {
        System.out.println("Help");
    }
}
