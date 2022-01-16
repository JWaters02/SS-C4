package ss.shell.utils;

public class BuiltIns {
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

    public enum UserTypes {
        STANDARD,
        SUPERUSER
    }

    public static final String HOME_PATH = "/home/bobby/SS/";

    public static boolean isBuiltIn(String command) {
        for (String builtIn : COMMANDS) {
            if (builtIn.equals(command)) {
                return true;
            }
        }
        return false;
    }
}
