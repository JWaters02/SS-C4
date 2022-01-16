package ss.shell;

import ss.shell.utils.BuiltIns;
import ss.shell.utils.Logs;
import ss.shell.utils.Filesystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;

public class BuiltInProcess {
    // TODO: Check if users have the permissions to run the command

    private final String[] command;
    private BuiltIns.UserTypes userType;
    private String username;

    public BuiltInProcess(String[] command) {
        this.command = command;
        this.userType = BuiltIns.UserTypes.STANDARD; // We assume that the user is a standard user until they log in
        this.username = null; // We don't know the username until they log in
    }

    /**
     * Execute the correct command based on command
     */
    public void execute() {
        switch (command[0].toLowerCase()) {
            case BuiltIns.SUPER -> {
                if (command.length == 1) {
                    Logs.printLine("Please specify a command.", Logs.LogLevel.ERROR);
                    return;
                }
                if (!Objects.equals(this.userType, BuiltIns.UserTypes.SUPERUSER)) {
                    Logs.printLine("You are not a super user!", Logs.LogLevel.ERROR);
                    return;
                }
                switch (command[1].toLowerCase()) {
                    case BuiltIns.ADDUSER -> addUser();
                    case BuiltIns.DELUSER -> deleteUser();
                    case BuiltIns.CHPASS -> chPass();
                    case BuiltIns.CHUSERTYPE -> chUserType();
                    default -> Logs.printLine("Cannot execute command as super: " + command[1], Logs.LogLevel.ERROR);
                }
            }
            case BuiltIns.LOGIN -> login();
            case BuiltIns.LOGOUT -> {
                if (this.username == null) {
                    Logs.printLine("You are not logged in!", Logs.LogLevel.ERROR);
                    return;
                }
                logout();
            }
            case BuiltIns.WHOAMI -> {
                if (this.username == null) {
                    Logs.printLine("You are not logged in!", Logs.LogLevel.ERROR);
                    return;
                }
                whoami();
            }
            case BuiltIns.MOVE -> {
                if (command.length != 3) {
                    Logs.printLine("Please provide only two arguments!", Logs.LogLevel.ERROR);
                    return;
                }
                move();
            }
            case BuiltIns.COPY -> {
                if (command.length != 3) {
                    Logs.printLine("Please provide only two arguments!", Logs.LogLevel.ERROR);
                    return;
                }
                copy();
            }
            case BuiltIns.HELP -> help();
            default -> Logs.printLine("Unknown command: " + command[0], Logs.LogLevel.ERROR);
        }
    }

    /**
     * Execute add user command.
     * command[0] is super
     * command[1] is adduser
     */
    private void addUser() {
        Logs.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Logs.print("Enter password: ");
        int password1 = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)
        Logs.print("Retype password: ");
        int password2 = scanner.nextLine().hashCode();
        if (password1 != password2) {
            Logs.printLine("Passwords do not match!", Logs.LogLevel.ERROR);
            return;
        }

        Logs.print("Enter user type: ");
        String userType = scanner.nextLine();
        if (!(userType.equalsIgnoreCase("super") || userType.equalsIgnoreCase("standard"))) {
            Logs.printLine("Invalid user type!", Logs.LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(username, String.valueOf(password1), BuiltIns.UserTypes.valueOf(userType.toUpperCase()));
        fs.createUser();
    }

    /**
     * Execute delete user command.
     * command[0] is super
     * command[1] is deluser
     */
    private void deleteUser() {
        Logs.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Filesystem fs = new Filesystem(username, null, null);
        fs.deleteUser();
    }

    /**
     * Execute change password command.
     * command[0] is super
     * command[1] is chpass
     */
    private void chPass() {
        Logs.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Filesystem fs = new Filesystem(username, null, null);
        if (!fs.hasDirectory()) {
            Logs.printLine("User does not exist!", Logs.LogLevel.ERROR);
            return;
        }

        Logs.print("Enter old password: ");
        int oldPassword = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)
        if (oldPassword != Integer.parseInt(fs.getPassword())) {
            Logs.printLine("Password incorrect!", Logs.LogLevel.ERROR);
            return;
        }
        Logs.print("Enter new password: ");
        int newPassword = scanner.nextLine().hashCode();
        fs.changePassword(String.valueOf(newPassword));
    }

    /**
     * Execute change user type command.
     * command[0] is super
     * command[1] is chusertype
     */
    private void chUserType() {
        Logs.print("Enter username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Filesystem fs = new Filesystem(username, null, null);
        if (!fs.hasDirectory()) {
            Logs.printLine("User does not exist!", Logs.LogLevel.ERROR);
            return;
        }

        Logs.printLine("Current user type: " + fs.getUserType(), Logs.LogLevel.INFO);
        Logs.printLine("Available user types:", Logs.LogLevel.INFO);
        Logs.print("Enter new user type: ");
        String userType = scanner.nextLine();
        if (!(userType.equalsIgnoreCase("super") || userType.equalsIgnoreCase("standard"))) {
            Logs.printLine("Invalid user type!", Logs.LogLevel.ERROR);
            return;
        }
        fs.changeUserType(BuiltIns.UserTypes.valueOf(userType.toUpperCase()));
    }

    /**
     * Execute login command.
     * command[0] is login
     */
    private void login() {
        Logs.print("Login$ ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Logs.print("Password$ ");
        int password = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)

        Filesystem fs = new Filesystem(username, String.valueOf(password), null);
        if (fs.login()) {
            Logs.printLine("Welcome " + username);
            this.username = username;
            this.userType = fs.getUserType();
        };
    }

    /**
     * Execute logout command.
     * command[0] is logout
     */
    private void logout() {
        Logs.printLine("logout" + this.username);
        this.username = null;
        this.userType = BuiltIns.UserTypes.STANDARD;
    }

    /**
     * Execute whoami command.
     * command[0] is whoami
     */
    private void whoami() {
        // If user is not logged in
        if (this.username == null) {
            Logs.printLine("Not logged in!", Logs.LogLevel.ERROR);
            return;
        }
        Logs.printLine("Username: " + this.username + "\nUser type: " + this.userType);
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
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z0-9]+")) {
                    Logs.printLine("Invalid destination name!", Logs.LogLevel.ERROR);
                }

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
        String destination = command[2];

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z0-9]+")) {
                    Logs.printLine("Invalid destination name!", Logs.LogLevel.ERROR);
                }

                // Copy source file to destination
                Files.copy(f.toPath(), (new File(destination)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute help command.
     * command[0] is help
     */
    private void help() {
        Logs.printLine("Help");
    }
}
