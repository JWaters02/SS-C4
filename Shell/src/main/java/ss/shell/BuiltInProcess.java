package ss.shell;

import ss.shell.utils.Filesystem;
import ss.shell.utils.BuiltIns;
import ss.shell.utils.BuiltIns.*;
import ss.shell.utils.ConsoleColours;
import ss.shell.utils.Logs;
import ss.shell.utils.Logs.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;

public class BuiltInProcess {
    private UserTypes userType;
    private String username;
    private String[] command;
    private String cwd;
    private final Logs logs = new Logs();
    private final ShellType shellType;
    private boolean isLoggedIn;

    /**
     * Constructor for the class.
     * @param username The current username. "guest" is default.
     * @param cwd The current working directory. Home path is default.
     * @param type The current type of the user. "standard" is default.
     * @param shellType The type of shell being run. "remote" or "local".
     */
    public BuiltInProcess(String username, String cwd, UserTypes type, ShellType shellType) {
        this.username = username;
        this.cwd = cwd;
        this.userType = type;
        this.shellType = shellType;
        this.isLoggedIn = true;
    }

    /**
     * Override constructor for the class.
     * @param username The current username. "guest" is default.
     * @param cwd The current working directory. Home path is default.
     * @param type The current type of the user. "standard" is default.
     * @param isLoggedIn If the user is logged in or not.
     * @param shellType The type of shell being run. "remote" or "local".
     */
    public BuiltInProcess(String username, String cwd, UserTypes type, boolean isLoggedIn, ShellType shellType) {
        this.username = username;
        this.cwd = cwd;
        this.userType = type;
        this.shellType = shellType;
        this.isLoggedIn = isLoggedIn;
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
    public String getCWD() { return this.cwd; }

    /**
     * Gets the user type of the current user.
     * @return user type of the current user.
     */
    public UserTypes getUserType() { return this.userType; }

    /**
     * Gets the boolean of if the user is logged in or not.
     * @return the boolean of if the user is logged in or not.
     */
    public boolean getIsLoggedIn() { return this.isLoggedIn; }

    /**
     * Gets the output logs object.
     * @return the output logs object.
     */
    public String getLogsOutput() { return this.logs.getOutputTotal(); }

    /**
     * Execute the correct command based on command
     */
    public void execute(String[] command) {
        this.command = command;
        switch (command[0].toLowerCase()) {
            case BuiltIns.SUPER -> {
                if (command.length == 1) {
                    print("Please specify a command.", LogLevel.ERROR);
                    return;
                }
                if (!Objects.equals(this.userType, BuiltIns.UserTypes.SUPERUSER)) {
                    print("You are not a super user!", LogLevel.ERROR);
                    return;
                }
                if (shellType == ShellType.LOCAL) {
                    switch (command[1].toLowerCase()) {
                        case BuiltIns.ADDUSER -> addUser();
                        case BuiltIns.DELUSER -> deleteUser();
                        case BuiltIns.CHPASS -> chPass();
                        case BuiltIns.CHUSERTYPE -> chUserType();
                        case BuiltIns.LISTUSERS -> listUsers();
                        case BuiltIns.HISTORY -> {
                            if (command.length != 3) {
                                print("Please provide a date in the format YYYY-mm-dd!", LogLevel.ERROR);
                                return;
                            }
                            history(command[2]);
                        }
                        default -> Logs.printLine("Cannot execute command as super: " + command[1], LogLevel.ERROR);
                    }
                } else {
                    switch (command[1].toLowerCase()) {
                        case BuiltIns.ADDUSER -> addUser(command);
                        case BuiltIns.DELUSER -> deleteUser(command);
                        case BuiltIns.CHPASS -> chPass(command);
                        case BuiltIns.CHUSERTYPE -> chUserType(command);
                        case BuiltIns.LISTUSERS -> listUsers();
                        case BuiltIns.HISTORY -> {
                            if (command.length != 3) {
                                print("Please provide a date in the format YYYY-mm-dd!", LogLevel.ERROR);
                                return;
                            }
                            history(command[2]);
                        }
                        default -> this.logs.outputInfo("Cannot execute command as super: " + command[1],
                                Store.YES, LogLevel.ERROR);
                    }
                }
            }
            case BuiltIns.ADDUSER, BuiltIns.CHUSERTYPE, BuiltIns.CHPASS, BuiltIns.DELUSER, BuiltIns.LISTUSERS,
                    BuiltIns.HISTORY -> print("You must execute this command as super.", LogLevel.ERROR);
            case BuiltIns.LOGIN -> {
                if (shellType == ShellType.LOCAL) login();
                else login(command);
            }
            case BuiltIns.LOGOUT -> {
                if (Objects.equals(this.username, "guest")) {
                    print("You are not logged in!", LogLevel.ERROR);
                    return;
                }
                logout();
            }
            case BuiltIns.WHOAMI -> {
                if (Objects.equals(this.username, "guest")) {
                    print("You are not logged in!", LogLevel.ERROR);
                    return;
                }
                whoami();
            }
            case BuiltIns.MOVE -> {
                if (command.length != 3) {
                    print("Please provide two arguments!", LogLevel.ERROR);
                    return;
                }
                move();
            }
            case BuiltIns.COPY -> {
                if (command.length != 3) {
                    print("Please provide two arguments!", LogLevel.ERROR);
                    return;
                }
                copy();
            }
            case BuiltIns.CD -> {
                if (command.length > 2) {
                    print("Please only provide a single path!", LogLevel.ERROR);
                    return;
                }
                cd();
            }
            case BuiltIns.SHOWDIR -> showDir();
            case BuiltIns.HELP -> help();
            default -> print("Unknown command: " + command[0], LogLevel.ERROR);
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
        int password1 = scanner.nextLine().hashCode();
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

        Filesystem fs = new Filesystem(username, String.valueOf(password1),
                BuiltIns.UserTypes.valueOf(userType.toUpperCase()), shellType);
        fs.createUser();
    }

    /**
     * Execute add user command.
     * command[0] is super
     * command[1] is adduser
     * command[2] is username
     * command[3] is password
     * command[4] is confirmed password
     * command[5] is user type
     * @param command the command that is entered
     */
    private void addUser(String[] command) {
        if (command.length != 6) {
            this.logs.outputInfo("Please enter all parameters correctly!", Store.YES, LogLevel.ERROR);
            return;
        }

        String username = command[2];
        int pass1 = command[3].hashCode();
        int pass2 = command[4].hashCode();
        String type = command[5];
        if (pass1 != pass2) {
            this.logs.outputInfo("Passwords do not match!", Store.YES, LogLevel.ERROR);
            return;
        }

        if (!(type.equalsIgnoreCase("superuser") || type.equalsIgnoreCase("standard"))) {
            this.logs.outputInfo("Invalid user type!", Store.YES, LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(username, String.valueOf(pass1),
                BuiltIns.UserTypes.valueOf(type.toUpperCase()), shellType);
        fs.createUser();
        this.logs.addToOutputTotal(fs.getLogsOutput());
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

        Filesystem fs = new Filesystem(username, null, null, shellType);
        fs.deleteUser();
    }

    /**
     * Execute delete user command.
     * command[0] is super
     * command[1] is deluser
     * command[2] is username
     * @param command the command that is entered
     */
    private void deleteUser(String[] command) {
        if (command.length != 3) {
            this.logs.outputInfo("Please enter all parameters correctly!", Store.YES, LogLevel.ERROR);
            return;
        }

        // Don't allow deletion of current user
        if (Objects.equals(command[2], this.username)) {
            this.logs.outputInfo("You cannot delete yourself!", Store.YES, LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(command[2], null, null, shellType);
        fs.deleteUser();
        this.logs.addToOutputTotal(fs.getLogsOutput());
    }

    /**
     * Lists the users in the system.
     * command[0] is super
     * command[1] is listusers
     */
    private void listUsers() {
        Filesystem fs = new Filesystem(this.cwd);
        File[] users = Filesystem.getUserFiles();
        if (users.length == 0) {
            print("No users found!", LogLevel.WARNING);
            return;
        }

        if (shellType == ShellType.LOCAL) {
            Logs.printLine("Users:", LogLevel.INFO);
            for (String user : fs.listUsers(users)) {
                Logs.printLine(user, LogLevel.INFO);
            }
        } else {
            this.logs.outputInfo("Users:", Store.NO, LogLevel.INFO);
            for (String user : fs.listUsers(users)) {
                this.logs.outputInfo("\n" + user, Store.NO, LogLevel.INFO);
            }
        }
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
        Filesystem fs = new Filesystem(username, null, null, shellType);
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
        int oldPassword = scanner.nextLine().hashCode();
        if (oldPassword != Integer.parseInt(fs.getPassword())) {
            Logs.printLine("Password incorrect!", LogLevel.ERROR);
            return;
        }
        Logs.print("Enter new password: ", Store.NO);
        int newPassword = scanner.nextLine().hashCode();
        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType(), shellType);
        if (fs2.changePassword(String.valueOf(newPassword))) {
            Logs.printLine("Password changed successfully!", LogLevel.INFO);
        }
    }

    /**
     * Execute change password command.
     * command[0] is super
     * command[1] is chpass
     * command[2] is username
     * command[3] is old password
     * command[4] is new password
     * @param command the command that is entered
     */
    private void chPass(String[] command) {
        if (command.length != 5) {
            this.logs.outputInfo("Please enter all parameters correctly!", Store.YES, LogLevel.ERROR);
            return;
        }

        String username = command[2];
        int oldPassword = command[3].hashCode();
        int newPassword = command[4].hashCode();

        Filesystem fs = new Filesystem(username, null, null, shellType);
        if (!fs.hasDirectory()) {
            this.logs.outputInfo("User does not exist!", Store.YES, LogLevel.ERROR);
            return;
        }
        this.logs.addToOutputTotal(fs.getLogsOutput());

        // Don't allow change password of current user
        if (Objects.equals(username, this.username)) {
            this.logs.outputInfo("You cannot change your own password!", Store.YES, LogLevel.ERROR);
            return;
        }

        if (oldPassword != Integer.parseInt(fs.getPassword())) {
            this.logs.outputInfo("Password incorrect!", Store.YES, LogLevel.ERROR);
            return;
        }

        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType(), shellType);
        if (fs2.changePassword(String.valueOf(newPassword))) {
            this.logs.outputInfo("Password changed successfully!", Store.YES, LogLevel.INFO);
        }
        this.logs.addToOutputTotal(fs2.getLogsOutput());
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

        Filesystem fs = new Filesystem(username, null, null, shellType);
        if (!fs.hasDirectory()) {
            Logs.printLine("User does not exist!", LogLevel.ERROR);
            return;
        }

        Logs.printLine("Current user type: " + fs.getUserType(), LogLevel.INFO);
        Logs.printLine("Available user types: standard, superuser", LogLevel.INFO);
        Logs.print("Enter new user type: ", Store.NO);
        String userType = scanner.nextLine();
        if (!(userType.equalsIgnoreCase("superuser") || userType.equalsIgnoreCase("standard"))) {
            Logs.printLine("Invalid user type!", LogLevel.ERROR);
            return;
        }
        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType(), shellType);
        if (fs2.changeUserType(BuiltIns.UserTypes.valueOf(userType.toUpperCase()))) {
            this.userType = fs2.getUserType();
            Logs.printLine("User type changed!", LogLevel.INFO);
        }
    }

    /**
     * Execute change user type command.
     * command[0] is super
     * command[1] is chusertype
     * command[2] is username
     * command[3] is new user type
     * @param command the command that is entered
     */
    private void chUserType(String[] command) {
        if (command.length != 4) {
            this.logs.outputInfo("Please enter all parameters correctly!", Store.YES, LogLevel.ERROR);
            return;
        }

        String username = command[2];
        String newType = command[3];

        // Don't allow changing of current user
        if (Objects.equals(username, this.username)) {
            this.logs.outputInfo("You cannot change your own user type!", Store.YES, LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(username, null, null, shellType);
        if (!fs.hasDirectory()) {
            this.logs.outputInfo("User does not exist!", Store.YES, LogLevel.ERROR);
            return;
        }
        this.logs.addToOutputTotal(fs.getLogsOutput());

        this.logs.outputInfo("Current user type: " + fs.getUserType(), Store.NO, LogLevel.INFO);
        this.logs.outputInfo("Available user types: standard, superuser", Store.YES, LogLevel.INFO);
        if (!(newType.equalsIgnoreCase("superuser") || newType.equalsIgnoreCase("standard"))) {
            this.logs.outputInfo("Invalid user type!", Store.YES, LogLevel.ERROR);
            return;
        }

        Filesystem fs2 = new Filesystem(username, fs.getPassword(), fs.getUserType(), shellType);
        if (fs2.changeUserType(BuiltIns.UserTypes.valueOf(newType.toUpperCase()))) {
            this.userType = fs2.getUserType();
            this.logs.outputInfo("User type changed!", Store.YES, LogLevel.INFO);
        }
        this.logs.addToOutputTotal(fs2.getLogsOutput());
    }

    /**
     * Execute login command.
     * command[0] is login
     */
    private void login() {
        // If already logged in, ignore
        if (!Objects.equals(this.username, "guest")) {
            Logs.printLine("Already logged in!", LogLevel.WARNING);
            return;
        }

        Logs.print("Login$ ", Store.NO);
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Logs.print("Password$ ", Store.NO);
        int password = scanner.nextLine().hashCode();

        Filesystem fs = new Filesystem(username, String.valueOf(password), null, shellType);
        if (fs.login()) {
            Logs.printLine("Welcome " + username, Store.YES);
            this.username = username;
            this.userType = fs.getUserType();
            this.cwd = BuiltIns.HOME_PATH + this.username;
        }
    }

    /**
     * Execute login command.
     * command[0] is login
     * command[1] is username
     * command[2] is password
     * @param command the command that is entered
     */
    private void login(String[] command) {
        if (command.length != 3) {
            this.logs.outputInfo("Please enter all parameters correctly!", Store.YES, LogLevel.ERROR);
            return;
        }

        // If already logged in, ignore
        if (this.isLoggedIn) {
            this.logs.outputInfo("You are already logged in!", Store.YES, LogLevel.WARNING);
            return;
        }

        String username = command[1];
        int password = command[2].hashCode();

        Filesystem fs = new Filesystem(username, String.valueOf(password), null, shellType);
        if (fs.login()) {
            this.logs.outputInfo("Welcome " + username, Store.YES, LogLevel.INFO);
            this.username = username;
            this.userType = fs.getUserType();
            this.cwd = BuiltIns.HOME_PATH + this.username;
            this.isLoggedIn = true;
        }
        this.logs.addToOutputTotal(fs.getLogsOutput());
    }

    /**
     * Execute logout command.
     * command[0] is logout
     */
    private void logout() {
        if (!this.isLoggedIn) {
            print("You are not logged in!", LogLevel.ERROR);
            return;
        }
        print("Logged out " + this.username, LogLevel.INFO);
        this.username = "guest";
        this.userType = BuiltIns.UserTypes.STANDARD;
        this.cwd = BuiltIns.HOME_PATH;
        this.isLoggedIn = false;
    }

    /**
     * Execute whoami command.
     * command[0] is whoami
     */
    private void whoami() {
        // If user is not logged in
        if (!this.isLoggedIn) {
            print("You are not logged in!", LogLevel.ERROR);
            return;
        }
        if (this.shellType == ShellType.LOCAL) {
            Logs.printLine("Username: " + this.username + "\nUser type: " + this.userType, Store.YES);
        } else {
            this.logs.outputInfo("Username: " + this.username + "\nUser type: " + this.userType, Store.YES,
                    LogLevel.INFO);
        }
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

        // If the source or destination paths are outside the user's home directory, don't let them
        if (source.contains("..") || source.startsWith("./") ||
                destination.contains("..") || destination.startsWith("./")) {
            print("You cannot copy files outside of your home directory!", LogLevel.ERROR);
            return;
        }

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z\\d]+")) {
                    print("Invalid destination name!", LogLevel.ERROR);
                }

                // Move file to destination
                f.renameTo(new File(destination));
                print("Moved " + source + " to " + destination, LogLevel.INFO);
            } else {
                print("File does not exist!", LogLevel.ERROR);
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

        // If the source or destination paths are outside the user's home directory, don't let them
        if (source.contains("..") || source.startsWith("./") ||
                destination.contains("..") || destination.startsWith("./")) {
            print("You cannot copy files outside of your home directory!", LogLevel.ERROR);
            return;
        }

        try {
            File f = new File(source);
            // Check if source file exists in directory
            if (f.exists()) {
                // Check if destination name does not contain any illegal characters (i.e. any punctuation)
                if (!destination.matches("[a-zA-Z\\d]+")) {
                    print("Invalid destination name!", LogLevel.ERROR);
                    return;
                }

                // Copy source file to destination
                Files.copy(f.toPath(), (new File(destination)).toPath(), StandardCopyOption.REPLACE_EXISTING);
                print("Copied " + source + " to " + destination, LogLevel.INFO);
            } else {
                print("File does not exist!", LogLevel.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute cd command.
     * command[0] is cd
     * command[1] is the new path
     */
    private void cd() {
        // If command is just cd, go back to root of folder
        if (command.length == 1) {
            this.cwd = BuiltIns.HOME_PATH + this.username;
        } else {
            if (command[1].equals("..")) {
                // If user is trying to go back to parent folder, go back to root of folder
                if (this.cwd.equals(BuiltIns.HOME_PATH + this.username)) {
                    print("You are already in the root folder!", LogLevel.ERROR);
                    return;
                } else {
                    String[] cwdSplit = this.cwd.split("/");
                    String[] newCwdSplit = new String[cwdSplit.length - 1];
                    System.arraycopy(cwdSplit, 0, newCwdSplit, 0, cwdSplit.length);
                    this.cwd = String.join("/", newCwdSplit);
                }
            } else if (!command[1].startsWith("./")) {
                // Get all dirs inside of user dir
                Filesystem fs = new Filesystem(this.cwd);
                String[] dirs = fs.getDirsInUserDir();
                boolean dirExists = false;
                for (String dir : dirs) {
                    if (Objects.equals(dir, command[1])) {
                        dirExists = true;
                        break;
                    }
                }
                if (!dirExists) {
                    print("Please enter a valid path! You cannot exit your local area!", LogLevel.ERROR);
                    return;
                }
                this.cwd += "/" + command[1];
            } else {
                print("Please enter a valid path! You cannot exit your local area!", LogLevel.ERROR);
            }
        }
    }

    /**
     * Executes the show directory command. Only shows the path relative to the user's area.
     * command[0] is showdir
     */
    private void showDir() {
        // Don't show the home path, just the user's area
        String ret = this.cwd.replace(BuiltIns.HOME_PATH, "/");
        print(ret, LogLevel.INFO);
    }

    /**
     * Execute help command.
     * command[0] is help
     */
    private void help() {
        if (shellType == ShellType.LOCAL) {
            Logs.printLine(ConsoleColours.WHITE_BOLD + "Process builder commands:" + ConsoleColours.RESET, Store.NO);
            for (int i = 0; i < BuiltIns.PB_DESCS.length; i++) {
                Logs.printLine(BuiltIns.PB_COMMANDS[i] + ": " + BuiltIns.PB_DESCS[i], Store.NO);
            }
            Logs.printLine("", Store.NO);

            Logs.printLine(ConsoleColours.WHITE_BOLD + "Built-in commands:" + ConsoleColours.RESET, Store.NO);
            for (int i = 0; i < BuiltIns.DESCS.length; i++) {
                Logs.printLine(BuiltIns.COMMANDS[i] + ": " + BuiltIns.DESCS[i], Store.NO);
            }
        } else {
            this.logs.outputInfo("Process builder commands:", Store.NO, LogLevel.WARNING);
            for (int i = 0; i < BuiltIns.PB_DESCS.length; i++) {
                this.logs.outputInfo(BuiltIns.PB_COMMANDS[i] + ": " + BuiltIns.PB_DESCS[i], Store.NO, LogLevel.INFO);
            }
            this.logs.outputInfo("", Store.NO, LogLevel.INFO);

            this.logs.outputInfo("Built-in commands:", Store.NO, LogLevel.WARNING);
            for (int i = 0; i < BuiltIns.DESCS.length; i++) {
                this.logs.outputInfo(BuiltIns.COMMANDS[i] + ": " + BuiltIns.DESCS[i], Store.NO, LogLevel.INFO);
            }
        }
    }

    /**
     * Execute history command.
     * command[0] is history
     * command[1] is the date
     * @param date date of logs to access the logs from.
     */
    private void history(String date) {
        if (!date.matches("(\\d{4})-(\\d{2})-(\\d{2})")) {
            print("Please input a valid date!", LogLevel.ERROR);
            return;
        }

        Filesystem fs = new Filesystem(this.cwd);
        File[] logFiles = fs.getLogs(date + "_log.txt");
        if (logFiles == null || logFiles.length == 0) {
            print("There are no log files with this date!", LogLevel.ERROR);
            return;
        }

        String[] history = fs.getLogOutput(logFiles);
        if (shellType == ShellType.LOCAL) {
            print("History for " + date + ":", LogLevel.INFO);
            for (String line : history) {
                print(line, LogLevel.INFO);
            }
        } else {
            this.logs.outputInfo("History for " + date + ":", Store.NO, LogLevel.INFO);
            String[] lines = history[0].split("\n");
            for (String line : lines) {
                this.logs.outputInfo(line, Store.NO, LogLevel.INFO);
            }
        }
    }

    /**
     * Runs the print for either remote or local shell types.
     * @param output The string to print.
     * @param level The log level to print at.
     */
    private void print(String output, LogLevel level) {
        if (this.shellType == ShellType.LOCAL) Logs.printLine(output, level);
        else this.logs.outputInfo(output, Store.YES, level);
    }
}
