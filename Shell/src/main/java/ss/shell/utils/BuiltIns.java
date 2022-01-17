package ss.shell.utils;

public class BuiltIns {
    /**
     * Built in commands
     */
    public static final String SUPER = "super"; // Equivalent to sudo. Only valid for "super" users. A standard user cannot use this command.
    public static final String ADDUSER = "adduser"; // Add a new user to the system. Requires "super" privileges to run.
    public static final String DELUSER = "deluser"; // Delete a user from the system. Requires "super" privileges to run.
    public static final String CHPASS = "chpass"; // Change a user's password. Requires "super" privileges to run.
    public static final String CHUSERTYPE = "chusertype"; // Change a user's type. Requires "super" privileges to run.
    public static final String LOGIN = "login"; // Log into a user account. Default superuser with username and password is "root".
    public static final String LOGOUT = "logout"; // Log out of the current user account.
    public static final String WHOAMI = "whoami"; // Print the current user's username.
    public static final String MOVE = "move"; // Move a file from source to target destination.
    public static final String COPY = "copy"; // Copy a file from source to target destination.
    public static final String HELP = "help"; // Print a list of all available commands.
    public static final String[] COMMANDS = {SUPER, ADDUSER, DELUSER, CHPASS, CHUSERTYPE, LOGIN, LOGOUT, WHOAMI, MOVE, COPY, HELP};

    /**
     * Process builder allowed commands
     */
    public static final String PB_LS = "ls"; // List files in the current directory.
    public static final String PB_CP = "cp"; // Copy a file from source to target destination.
    public static final String PB_MV = "mv"; // Move a file from source to target destination.
    public static final String PB_MKDIR = "mkdir"; // Create a new directory.
    public static final String PB_RMDIR = "rmdir"; // Remove a directory.
    public static final String PB_HELP = "help"; // Print a list of all available commands.
    public static final String PB_PWD = "pwd"; // Print the current working directory.
    public static final String PB_PS = "ps"; // List all running processes.
    public static final String PB_WHICH = "which"; // Print the full path of a command.
    public static final String[] PB_COMMANDS = {PB_LS, PB_CP, PB_MV, PB_MKDIR, PB_RMDIR, PB_HELP, PB_PWD, PB_PS, PB_WHICH};

    /**
     * The different types of user groups.
     */
    public enum UserTypes {
        STANDARD,
        SUPERUSER
    }

//    public static final String HOME_PATH = "/home/bobby/SS/";
    public static final String HOME_PATH = "D:\\University\\Year 2\\SS\\";

    /**
     * Check if a command is a built-in command.
     * @param command Command to check.
     * @return True if command is a built-in command.
     */
    public static boolean isBuiltIn(String command) {
        for (String builtIn : COMMANDS) {
            if (builtIn.equals(command)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a command is a process builder command.
     * @param command Command to check.
     * @return True if command is a process builder command.
     */
    public static boolean isProcessBuilder(String command) {
        for (String pbCommand : PB_COMMANDS) {
            if (pbCommand.equals(command)) {
                return true;
            }
        }
        return false;
    }
}
