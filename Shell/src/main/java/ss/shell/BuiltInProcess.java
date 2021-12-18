package ss.shell;

import ss.shell.utils.BuiltIns;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class BuiltInProcess {
    // TODO: Check if users have the permissions to run the command

    private final String[] command;
    private final String prompt;

    public BuiltInProcess(String[] command, String prompt) {
        // TODO: Might want to pass through current user?
        this.command = command;
        this.prompt = prompt;
    }

    /**
     * Execute the correct command based on command
     */
    public void execute() {
        switch (command[0].toLowerCase()) {
            case BuiltIns.SUPER:
                if (command.length == 1) {
                    System.out.println("Please specify a command.");
                    return;
                }
                switch (command[1].toLowerCase()) {
                    case BuiltIns.ADDUSER:
                        addUser();
                        break;
                    case BuiltIns.CHPASS:
                        chPass();
                        break;
                    default:
                        System.out.println("Cannot execute command as super: " + command[1]);
                        return;
                }
                break;
            case BuiltIns.WHOAMI:
                whoami();
                break;
            case BuiltIns.MOVE:
                if (command.length != 3) {
                    System.out.println("Please provide only two arguments!");
                    return;
                }
                move();
                break;
            case BuiltIns.COPY:
                if (command.length != 3) {
                    System.out.println("Please provide only two arguments!");
                    return;
                }
                copy();
                break;
            case BuiltIns.LOGOUT:
                logout();
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
        // TODO: Remove user from database if exists
    }

    /**
     * Execute change password command.
     * command[0] is super
     * command[1] is chpass
     */
    private void chPass() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        // TODO: Check if user exists

        System.out.print("Enter old password: ");
        int oldPassword = scanner.nextLine().hashCode(); // TODO: Mask password input
        int oldPasswordCheck = 0; // TODO: Get from database
        if (oldPasswordCheck != oldPassword) {
            System.out.println("Password incorrect!");
            return;
        }

        System.out.print("Enter new password: ");
        int newPassword = scanner.nextLine().hashCode();
        // TODO: Change password hash on user db
    }

    /**
     * Execute change user type command.
     * command[0] is super
     * command[1] is chusertype
     */
    private void chUserType() {
        System.out.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        // TODO: Check if user exists and print current group + list of groups

        System.out.print("Enter new user type: ");
        String userType = scanner.nextLine();
        // TODO: Validate user type + change user type in database
    }

    /**
     * Execute whoami command.
     * command[0] is whoami
     */
    private void whoami() {
        // TODO: Get current user's username and group from database
        System.out.println("Username: " + "\nUser type: ");
    }

    /**
     * Execute move command.
     * command[0] is move
     * command[1] is source
     * command[2] is destination
     */
    private void move() {
        String source = command[1];
        String destination = command[2];

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Move file to destination
                f.renameTo(new File(destination));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute copy command.
     * command[0] is copy
     * command[1] is source
     * command[2] is destination
     */
    private void copy() {
        String source = command[1];
        String destination = command[2]; // TODO: Might want to check if destination is valid?

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Copy source file to destination
                Files.copy(f.toPath(), (new File(destination)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute login command.
     * command[0] is login
     */
    public void login() {
        System.out.print("Login$ ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        // TODO: Check user is valid

        System.out.print("Password$ ");
        int password = scanner.nextLine().hashCode(); // TODO: Mask password input
        // TODO: Check password is valid

        System.out.println("Welcome " + username);
    }

    /**
     * Execute logout command.
     * command[0] is logout
     */
    private void logout() {

    }

    /**
     * Execute help command.
     * command[0] is help
     */
    private void help() {
        System.out.println("Help");
    }
}
