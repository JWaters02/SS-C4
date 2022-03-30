package ss.shell.utils;

public class BuiltIns {
    /**
     * Built in commands
     */
    public static final String SUPER = "super"; // Equivalent to sudo. Only valid for "super" users. A standard user cannot use this command.
    public static final String ADDUSER = "adduser"; // Add a new user to the system. Requires "super" privileges to run.
    public static final String DELUSER = "deluser"; // Delete a user from the system. Requires "super" privileges to run.
    public static final String LISTUSERS = "listusers"; // Lists all users on the system. Requires "super" privileges to run.
    public static final String CHPASS = "chpass"; // Change a user's password. Requires "super" privileges to run.
    public static final String CHUSERTYPE = "chusertype"; // Change a user's type. Requires "super" privileges to run.
    public static final String LOGIN = "login"; // Log into a user account. Default superuser with username and password is "admin", "pass".
    public static final String LOGOUT = "logout"; // Log out of the current user account.
    public static final String WHOAMI = "whoami"; // Print the current user's username.
    public static final String MOVE = "move"; // Move a file from source to target destination.
    public static final String COPY = "copy"; // Copy a file from source to target destination.
    public static final String CD = "cd"; // Change the current directory.
    public static final String SHOWDIR = "showdir"; // Show current working directory.
    public static final String HELP = "help"; // Print a list of all available commands.
    public static final String HISTORY = "history"; // Prints the logs based on a given date. Requires "super" privileges to run.
    public static final String[] COMMANDS = {SUPER, ADDUSER, DELUSER, LISTUSERS, CHPASS, CHUSERTYPE, LOGIN, LOGOUT, WHOAMI, MOVE, COPY, CD, SHOWDIR, HELP, HISTORY};

    private static final String SUPER_DESC = "Equivalent to sudo. Only valid for \"super\" users. A standard user cannot use this command.";
    private static final String ADDUSER_DESC = "Add a new user to the system. Requires \"super\" privileges to run. Usage: super adduser <username> <password> <confirm password> <type>";
    private static final String DELUSER_DESC = "Delete a user from the system. Requires \"super\" privileges to run. Usage: super deluser <username>";
    private static final String LISTUSERS_DESC = "Lists all users on the system. Requires \"super\" privileges to run.";
    private static final String CHPASS_DESC = "Change a user's password. Requires \"super\" privileges to run. Usage: super chpass <username> <old password> <new password>";
    private static final String CHUSERTYPE_DESC = "Change a user's type. Requires \"super\" privileges to run.";
    private static final String LOGIN_DESC = "Log into a user account. Usage: login <username> <password>";
    private static final String LOGOUT_DESC = "Log out of the current user account.";
    private static final String WHOAMI_DESC = "Print the current user's username.";
    private static final String MOVE_DESC = "Move a file from source to target destination. Usage: move <source> <destination>";
    private static final String COPY_DESC = "Copy a file from source to target destination. Usage: copy <source> <destination>";
    private static final String CD_DESC = "Change the current directory. Usage: cd <path> No preceding path will return to the home path.";
    private static final String SHOWDIR_DESC = "Show current working directory.";
    private static final String HELP_DESC = "Print a list of all available commands.";
    private static final String HISTORY_DESC = "Prints the logs based on a given date. Date format: YYYY-mm-dd e.g. 2022-03-06. Requires \"super\" privileges to run.";
    public static final String[] DESCS = {SUPER_DESC, ADDUSER_DESC, DELUSER_DESC, LISTUSERS_DESC, CHPASS_DESC, CHUSERTYPE_DESC, LOGIN_DESC, LOGOUT_DESC, WHOAMI_DESC, MOVE_DESC, COPY_DESC, CD_DESC, SHOWDIR_DESC, HELP_DESC, HISTORY_DESC};

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

    private static final String PB_LS_DESC = "List files in the current directory.";
    private static final String PB_CP_DESC = "Copy a file from source to target destination.";
    private static final String PB_MV_DESC = "Move a file from source to target destination.";
    private static final String PB_MKDIR_DESC = "Create a new directory.";
    private static final String PB_RMDIR_DESC = "Remove a directory.";
    private static final String PB_HELP_DESC = "Print a list of all available commands.";
    private static final String PB_PWD_DESC = "Print the current working directory.";
    private static final String PB_PS_DESC = "List all running processes.";
    private static final String PB_WHICH_DESC = "Print the full path of a command.";
    public static final String[] PB_DESCS = {PB_LS_DESC, PB_CP_DESC, PB_MV_DESC, PB_MKDIR_DESC, PB_RMDIR_DESC, PB_HELP_DESC, PB_PWD_DESC, PB_PS_DESC, PB_WHICH_DESC};

    /**
     * The different types of user groups.
     */
    public enum UserTypes {
        STANDARD,
        SUPERUSER
    }

    /**
     * The different types of shells that can be run on this program
     */
    public enum ShellType {
        LOCAL,
        REMOTE
    }

    public static String HOME_PATH = System.getProperty("user.dir") + "/src/main/resources/home/";

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
