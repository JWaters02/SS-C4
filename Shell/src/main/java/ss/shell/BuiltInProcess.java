package ss.shell;

import ss.shell.utils.Filesystem;
import ss.shell.utils.BuiltIns;
import ss.shell.utils.ConsoleColours;
import ss.shell.utils.Logs;
import ss.shell.utils.Logs.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;

public class BuiltInProcess {
    private BuiltIns.UserTypes userType;
    private String username;
    private String[] command;
    private String cwd;

    public BuiltInProcess() {
        this.userType = BuiltIns.UserTypes.STANDARD; // We assume that the user is a standard user until they log in
        this.username = null; // We don't know the username until they log in
        this.cwd = BuiltIns.HOME_PATH; // Original home path for guests
    }

    /**
     * Gets the username of the current user.
     * @return username of the current user.
     */
    public String getUsername() {  return this.username; }

    /**
     * Gets the cwd of the current user.
     * @return cwd of the current user.
     */
    public String getCWD() {return this.cwd; }

    /**
     * Execute the correct command based on command
     */
    public void execute(String[] command) {
        this.command = command;
        switch (command[0].toLowerCase()) {
            case BuiltIns.SUPER -> {
                if (command.length == 1) {
                    Logs.printLine("Please specify a command.", LogLevel.ERROR);
                    return;
                }
                if (!Objects.equals(this.userType, BuiltIns.UserTypes.SUPERUSER)) {
                    Logs.printLine("You are not a super user!", LogLevel.ERROR);
                    return;
                }
                switch (command[1].toLowerCase()) {
                    case BuiltIns.ADDUSER -> addUser();
                    case BuiltIns.DELUSER -> deleteUser();
                    case BuiltIns.CHPASS -> chPass();
                    case BuiltIns.CHUSERTYPE -> chUserType();
                    default -> Logs.printLine("Cannot execute command as super: " + command[1], LogLevel.ERROR);
                }
            }
            case BuiltIns.ADDUSER, BuiltIns.CHUSERTYPE, BuiltIns.CHPASS, BuiltIns.DELUSER -> {
                Logs.printLine("You must execute this command as super.", LogLevel.ERROR);
            }
            case BuiltIns.LOGIN -> login();
            case BuiltIns.LOGOUT -> {
                if (this.username == null) {
                    Logs.printLine("You are not logged in!", LogLevel.ERROR);
                    return;
                }
                logout();
            }
            case BuiltIns.WHOAMI -> {
                if (this.username == null) {
                    Logs.printLine("You are not logged in!", LogLevel.ERROR);
                    return;
                }
                whoami();
            }
            case BuiltIns.MOVE -> {
                if (command.length != 3) {
                    Logs.printLine("Please provide two arguments!", LogLevel.ERROR);
                    return;
                }
                move();
            }
            case BuiltIns.COPY -> {
                if (command.length != 3) {
                    Logs.printLine("Please provide two arguments!", LogLevel.ERROR);
                    return;
                }
                copy();
            }
            case BuiltIns.CD -> {
                if (this.username == null) {
                    Logs.printLine("You are not logged in!", LogLevel.ERROR);
                    return;
                }
                if (command.length != 2) {
                    Logs.printLine("Please provide a path!", LogLevel.ERROR);
                    return;
                }
                cd(command[1]);
            }
            case BuiltIns.SHOWDIR -> showDir();
            case BuiltIns.HELP -> help();
            default -> Logs.printLine("Unknown command: " + command[0], LogLevel.ERROR);
        }
    }

    /**
     * Execute add user command.
     * command[0] is super
     * command[1] is adduser
     */
    private void addUser() {
        Logs.print("Enter username: ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Logs.print("Enter password: ", Store.NO);
        int password1 = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)
        Logs.print("Retype password: ", Store.NO);
        int password2 = scanner.nextLine().hashCode();
        if (password1 != password2) {
            Logs.printLine("Passwords do not match!", LogLevel.ERROR);
            return;
        }

        Logs.print("Enter user type: ", Store.NO);
        String userType = scanner.nextLine();
        if (!(userType.equalsIgnoreCase("superuser") || userType.equalsIgnoreCase("standard"))) {
            Logs.printLine("Invalid user type!", LogLevel.ERROR);
            return;
        }

        // TODO: Fix type user inputted "superuser" actually creating user with type STANDARD
        Filesystem fs = new Filesystem(username, String.valueOf(password1),
                BuiltIns.UserTypes.valueOf(userType.toUpperCase()));
        fs.createUser();
    }

    /**
     * Execute delete user command.
     * command[0] is super
     * command[1] is deluser
     */
    private void deleteUser() {
        Logs.print("Enter username: ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        // Don't allow deletion of current user
        if (Objects.equals(username, this.username)) {
            Logs.printLine("You cannot delete yourself!", LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(username, null, null);
        fs.deleteUser();
    }

    /**
     * Execute change password command.
     * command[0] is super
     * command[1] is chpass
     */
    private void chPass() {
        Logs.print("Enter username: ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Filesystem fs = new Filesystem(username, null, null);
        if (!fs.hasDirectory()) {
            Logs.printLine("User does not exist!", LogLevel.ERROR);
            return;
        }

        // Don't allow change password of current user
        if (Objects.equals(username, this.username)) {
            Logs.printLine("You cannot change your own password!", LogLevel.ERROR);
            return;
        }

        Logs.print("Enter old password: ", Store.NO);
        int oldPassword = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)
        if (oldPassword != Integer.parseInt(fs.getPassword())) {
            Logs.printLine("Password incorrect!", LogLevel.ERROR);
            return;
        }
        Logs.print("Enter new password: ", Store.NO);
        int newPassword = scanner.nextLine().hashCode();
        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType());
        if (fs2.changePassword(String.valueOf(newPassword))) {
            Logs.printLine("Password changed successfully!", LogLevel.INFO);
        }
    }

    /**
     * Execute change user type command.
     * command[0] is super
     * command[1] is chusertype
     */
    private void chUserType() {
        Logs.print("Enter username: ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        // Don't allow changing of current user
        if (Objects.equals(username, this.username)) {
            Logs.printLine("You cannot change your own user type!", LogLevel.WARNING);
            return;
        }

        Filesystem fs = new Filesystem(username, null, null);
        if (!fs.hasDirectory()) {
            Logs.printLine("User does not exist!", LogLevel.ERROR);
            return;
        }

        Logs.printLine("Current user type: " + fs.getUserType(), LogLevel.INFO);
        // TODO: Maybe everything that isn't current
        Logs.printLine("Available user types: standard, superuser", LogLevel.INFO);
        Logs.print("Enter new user type: ", Store.NO);
        String userType = scanner.nextLine();
        if (!(userType.equalsIgnoreCase("superuser") || userType.equalsIgnoreCase("standard"))) {
            Logs.printLine("Invalid user type!", LogLevel.ERROR);
            return;
        }
        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType());
        if (fs2.changeUserType(BuiltIns.UserTypes.valueOf(userType.toUpperCase()))) {
            this.userType = fs2.getUserType();
            Logs.printLine("User type changed!", LogLevel.INFO);
        }
    }

    /**
     * Execute login command.
     * command[0] is login
     */
    private void login() {
        // If already logged in, ignore
        if (this.username != null) {
            Logs.printLine("Already logged in!", LogLevel.WARNING);
            return;
        }

        Logs.print("Login$ ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Logs.print("Password$ ", Store.NO);
        int password = scanner.nextLine().hashCode(); // TODO: Mask password input (wait until we have JavaFX UI)

        Filesystem fs = new Filesystem(username, String.valueOf(password), null);
        if (fs.login()) {
            Logs.printLine("Welcome " + username);
            this.username = username;
            this.userType = fs.getUserType();
            this.cwd = BuiltIns.HOME_PATH + this.username;
        }
    }

    /**
     * Execute logout command.
     * command[0] is logout
     */
    private void logout() {
        Logs.printLine("logout " + this.username);
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
            Logs.printLine("Not logged in!", LogLevel.ERROR);
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
        // TODO: Need more outputs for edge cases (such as trying to move files outside of their user directory)
        // TODO: Fix
        String source = command[1];
        String destination = command[2];

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z0-9]+")) {
                    Logs.printLine("Invalid destination name!", LogLevel.ERROR);
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
        // TODO: Need more outputs for edge cases (such as trying to copy files outside of their user directory)
        // TODO: Fix
        String source = command[1];
        String destination = command[2];

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z0-9]+")) {
                    Logs.printLine("Invalid destination name!", LogLevel.ERROR);
                }

                // Copy source file to destination
                Files.copy(f.toPath(), (new File(destination)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute cd command.
     * command[0] is cd
     * command[1] is the new path
     * @param newPath New path
     */
    private void cd(String newPath) {
        // Must not go outside the user's directory
        Logs.printLine(newPath);
        if (!newPath.startsWith(this.cwd) || !newPath.startsWith("./")) {
            // Get all dirs inside of user dir
            Filesystem fs = new Filesystem(this.cwd);
            String[] dirs = fs.getDirsInUserDir();
            boolean dirExists = false;
            for (String dir : dirs) {
                if (Objects.equals(dir, newPath)) {
                    dirExists = true;
                    break;
                }
            }
            if (!dirExists) {
                Logs.printLine("Please enter a valid path! You cannot exit your local area!", LogLevel.ERROR);
                return;
            }
        }

        if (newPath.endsWith("/")) this.cwd += "/" + newPath;
        else this.cwd += "/" + newPath + "/";
        Logs.printLine(this.cwd);
    }

    private void showDir() {
        // Don't show the home path, just the user's area
        String ret = this.cwd.replace(BuiltIns.HOME_PATH, "/");
        Logs.printLine(ret);
    }

    /**
     * Execute help command.
     * command[0] is help
     */
    private void help() {
        Logs.printLine(ConsoleColours.WHITE_BOLD + "Process builder commands:" + ConsoleColours.RESET, Store.NO);
        for (int i = 0; i < BuiltIns.PB_DESCS.length; i++) {
            Logs.printLine(BuiltIns.PB_COMMANDS[i] + ": " + BuiltIns.PB_DESCS[i], Store.NO);
        }
        Logs.printLine("", Store.NO);

        Logs.printLine(ConsoleColours.WHITE_BOLD + "Built-in commands:" + ConsoleColours.RESET, Store.NO);
        for (int i = 0; i < BuiltIns.DESCS.length; i++) {
            Logs.printLine(BuiltIns.COMMANDS[i] + ": " + BuiltIns.DESCS[i], Store.NO);
        }
    }
}
