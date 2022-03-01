package ss.shell.utils;

import ss.shell.utils.Logs.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Filesystem {
    // TODO: Add encryption of files/folders

    private final String username;
    private String password;
    private BuiltIns.UserTypes type;
    private String cwd;

    /**
     * Constructor for the Filesystem class.
     * @param username the username of the user.
     * @param password the password of the user (hashed).
     * @param type the group type of the user.
     */
    public Filesystem(String username, String password, BuiltIns.UserTypes type) {
        this.username = username;
        this.password = password;
        this.type = type;
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
                Logs.printLine("Invalid password.", LogLevel.ERROR);
            }
        } else {
            Logs.printLine("User " + this.username + " does not exist!", LogLevel.ERROR);
        }
        return success;
    }

    /**
     * Creating a user needs to check if the user has a directory.
     */
    public void createUser() {
        if (!this.hasDirectory()) {
            Logs.printLine("User " + this.username + " does not have a directory, creating user directory.", LogLevel.INFO);
            this.createUserDir();
            this.writeUserDetails();
        } else {
            Logs.printLine("User " + this.username + " already exists!", LogLevel.WARNING);
        }
    }

    /**
     * Deletes the user directory and all of its contents.
     * Also deletes the user info file.
     */
    public void deleteUser() {
        if (this.hasDirectory()) {
            Logs.printLine("Deleting user " + this.username);
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
            Logs.printLine("User " + this.username + " does not exist!", LogLevel.ERROR);
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
            Logs.printLine("User " + this.username + " does not exist!", LogLevel.ERROR);
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
            Logs.printLine("User " + this.username + " does not exist!", LogLevel.ERROR);
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
    * We want to make a directory in home for each user that is created.
    * Then we can store all of their details in that directory.
     */
    private void createUserDir() {
        Logs.printLine("Creating directory for " + this.username, LogLevel.INFO);
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
            Logs.printLine("Wrote user details to " + BuiltIns.HOME_PATH + this.username + ".txt", LogLevel.INFO);
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
}
