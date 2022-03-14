package ss.shell.utils;

import ss.shell.utils.Logs.*;
import ss.shell.utils.BuiltIns.*;

import java.io.*;
import java.util.Scanner;

public class Filesystem {
    private final String username;
    private String password;
    private UserTypes type;
    private String cwd;
    private final Logs logs = new Logs();
    private ShellType shellType;

    /**
     * Constructor for the Filesystem class.
     * @param username The username of the user.
     * @param password The password of the user (hashed).
     * @param type The group type of the user.
     * @param shellType The type of shell being run for.
     */
    public Filesystem(String username, String password, UserTypes type, ShellType shellType) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.shellType = shellType;
    }

    /**
     * Override constructor for the Filesystem class.
     * @param cwd The current working directory of the user.
     */
    public Filesystem(String cwd) {
        this.username = null;
        this.cwd = cwd;
    }

    /**
     * Logging in needs to check if the user has a directory and if they do,
     * check if the password matches the user info file.
     * @return true if the user has a directory and the password matches.
     */
    public boolean login() {
        boolean success = false;
        if (this.hasDirectory()) {
            if (this.checkPassword()) {
                success = true;
            } else {
                print("Invalid password.", LogLevel.ERROR);
            }
        } else {
            print("User " + this.username + " does not exist!", LogLevel.ERROR);
        }
        return success;
    }

    /**
     * Creating a user needs to check if the user has a directory.
     */
    public void createUser() {
        if (!this.hasDirectory()) {
            print("User " + this.username + " does not have a directory, creating user directory.", LogLevel.INFO);
            this.createUserDir();
            this.writeUserDetails();
        } else {
            print("User " + this.username + " already exists!", LogLevel.WARNING);
        }
    }

    /**
     * Deletes the user directory and all of its contents.
     * Also deletes the user info file.
     */
    public void deleteUser() {
        if (this.hasDirectory()) {
            print("Deleting user " + this.username, LogLevel.INFO);
            try {
                File userDir = new File(this.getUserDirPath());
                // Empty directory first
                File[] files = userDir.listFiles();
                for (File file : files) {
                    file.delete();
                }
                userDir.delete();
                File userInfo = new File(BuiltIns.HOME_PATH + this.username + ".txt");
                userInfo.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            print("User " + this.username + " does not exist!", LogLevel.ERROR);
        }
    }

    /**
     * Gets the current password of the user.
     * @return the current password of the user.
     */
    public String getPassword() {
        String userInfo = this.getUserInfo();
        String ret = "";
        try {
            ret = userInfo.split("\n")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Gets the current group type of the user.
     * @return the current group type of the user.
     */
    public BuiltIns.UserTypes getUserType() {
        String userInfo = this.getUserInfo();
        BuiltIns.UserTypes ret = null;
        try {
            if (userInfo.split("\n")[2].equals("SUPERUSER")) {
                ret = BuiltIns.UserTypes.SUPERUSER;
            } else {
                ret = BuiltIns.UserTypes.STANDARD;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Change the password for the user.
     * @param newPassword the new password for the user.
     * @return true if the password was changed successfully.
     */
    public boolean changePassword(String newPassword) {
        boolean success = false;
        if (this.hasDirectory()) {
            success = true;
            this.password = newPassword;
            this.writeUserDetails();
        } else {
            print("User " + this.username + " does not exist!", LogLevel.ERROR);
        }
        return success;
    }

    /**
     * Change the group type for the user.
     * @param newType the new group type for the user.
     * @return true if the change was successful.
     */
    public boolean changeUserType(BuiltIns.UserTypes newType) {
        boolean success = false;
        if (this.hasDirectory()) {
            success = true;
            this.type = newType;
            this.writeUserDetails();
        } else {
            print("User " + this.username + " does not exist!", LogLevel.ERROR);
        }
        return success;
    }

    /**
     * Check if the user has a directory already.
     * @return true if the user has a directory.
     */
    public boolean hasDirectory() {
        return new File(BuiltIns.HOME_PATH + this.username).exists();
    }

    /**
     * Gets the directories in the user's directory.
     * @return list of directory names.
     */
    public String[] getDirsInUserDir() {
        File path = new File(this.cwd);
        return path.list();
    }

    /**
     * Gets the log files in the home directory (where they are stored).
     * @param filename the name of the log file to find.
     * @return the log file if it exists.
     */
    public File[] getLogs(String filename) {
        File homeDir = new File(BuiltIns.HOME_PATH);
        FilenameFilter filter = (file, s) -> s.toLowerCase().endsWith(filename);
        return homeDir.listFiles(filter);
    }

    /**
     * Returns the lines inside the log files provided.
     * @param logFiles the log files to read.
     * @return the lines inside the log files.
     */
    public String[] getLogOutput(File[] logFiles) {
        String[] logOutput = new String[logFiles.length];
        for (int i = 0; i < logFiles.length; i++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(logFiles[i]));
                String line = br.readLine();
                while (line != null) {
                    logOutput[i] += line + "\n";
                    line = br.readLine();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logOutput;
    }

    /**
     * Gets all the user files from the home directory.
     * @return the user files.
     */
    public File[] getUserFiles() {
        File homeDir = new File(BuiltIns.HOME_PATH);
        // Filter OUT the log files
        FilenameFilter filter = (file, s) -> !s.toLowerCase().endsWith(".txt");
        return homeDir.listFiles(filter);
    }

    /**
     * Lists the users from the user files in the home directory.
     * @param userFiles the user files to read.
     * @return the users from the user files.
     */
    public String[] listUsers(File[] userFiles) {
        String[] users = new String[userFiles.length];
        for (int i = 0; i < userFiles.length; i++) {
            users[i] = userFiles[i].getName();
        }
        return users;
    }

    /**
    * We want to make a directory in home for each user that is created.
    * Then we can store all of their details in that directory.
     */
    private void createUserDir() {
        print("Creating directory for " + this.username, LogLevel.INFO);
        new File(BuiltIns.HOME_PATH + this.username).mkdir();
    }

    /**
     * Get the path of the user's directory.
     * @return the path of the user's directory.
     */
    private String getUserDirPath() {
        return BuiltIns.HOME_PATH + this.username + "/";
    }

    /**
     * Write the user's login details to a text file in their directory.
     * We actually want to store the file outside the user directory
     * because we don't want to store the password inside the user's directory.
     */
    private void writeUserDetails() {
        try {
            FileWriter writer = new FileWriter(BuiltIns.HOME_PATH + this.username + ".txt");
            writer.write(this.username + "\n");
            writer.write(this.password + "\n");
            if (this.type != null) {
                writer.write(this.type.toString());
            }
            writer.close();
            print("Wrote user details to " + BuiltIns.HOME_PATH + this.username + ".txt", LogLevel.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the password matches the user info file.
     * @return true if the password matches the user info file.
     */
    private boolean checkPassword() {
        return getPassword().equals(this.password);
    }

    /**
     * Get the user info file.
     * @return the user info file.
     */
    private String getUserInfo() {
        StringBuilder userInfo = new StringBuilder();
        try {
            FileReader file = new FileReader(BuiltIns.HOME_PATH + this.username + ".txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                userInfo.append(reader.nextLine()).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo.toString();
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

    /**
     * Gets the output logs object.
     * @return the output logs object.
     */
    public String getLogsOutput() { return this.logs.getOutputTotal(); }
}
