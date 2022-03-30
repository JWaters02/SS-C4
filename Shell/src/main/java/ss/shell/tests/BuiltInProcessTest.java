package ss.shell.tests;

import org.junit.Before;
import org.junit.Test;
import ss.shell.BuiltInProcess;
import ss.shell.utils.BuiltIns;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class BuiltInProcessTest {
    private String username;
    private String cwd;
    private BuiltIns.UserTypes userType;
    private boolean isLoggedIn;
    private BuiltIns.ShellType shellType;

    public BuiltInProcessTest() {
    }

    @Before
    public void setUp() {
        this.username = "guest";
        BuiltIns.HOME_PATH = System.getProperty("user.dir") + "/src/main/resources/tests/";
        this.cwd = BuiltIns.HOME_PATH;
        this.userType = BuiltIns.UserTypes.STANDARD;
        this.isLoggedIn = false;
        this.shellType = BuiltIns.ShellType.REMOTE;
    }

    @Test
    public void getUsername() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        assertEquals(this.username, builtInProcess.getUsername());
    }

    @Test
    public void getCWD() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        assertEquals(this.cwd, builtInProcess.getCWD());
    }

    @Test
    public void getUserType() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        assertEquals(this.userType, builtInProcess.getUserType());
    }

    @Test
    public void getIsLoggedIn() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        assertEquals(this.isLoggedIn, builtInProcess.getIsLoggedIn());
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd, this.userType, true,
                this.shellType);
        assertTrue(builtInProcess2.getIsLoggedIn());
    }

    @Test
    public void getLogsOutput() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        assertEquals("", builtInProcess.getLogsOutput());
    }

    @Test
    public void executeSuper() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"super"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please specify a command"));
    }

    @Test
    public void executeNotSuperUser() {
        this.userType = BuiltIns.UserTypes.STANDARD;
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"super", "adduser"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are not a super user!"));
    }

    @Test
    public void executeNotSuperCommand() {
        this.userType = BuiltIns.UserTypes.SUPERUSER;
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"super", "whoami"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Cannot execute command as super: whoami"));
    }

    @Test
    public void executeNotSuperWithSuper() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"adduser"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You must execute this command as super."));
    }

    @Test
    public void executeCommandsNotLoggedIn() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"whoami"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are not logged in!"));

        command = new String[]{"logout"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are not logged in!"));
    }

    @Test
    public void executeWrongNumOfParams() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"move", "source"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please provide two arguments!"));

        command = new String[]{"copy", "source"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please provide two arguments!"));

        command = new String[]{"copy", "source", "random path", "destination"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please provide two arguments!"));

        command = new String[]{"cd", "path 1", "path 2"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please only provide a single path!"));
    }

    @Test
    public void executeUnknownCommand() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"random"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Unknown command: random"));
    }

    @Test
    public void executeInvalidHistory() {
        this.userType = BuiltIns.UserTypes.SUPERUSER;
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd, this.userType, this.isLoggedIn,
                this.shellType);
        String[] command = {"super", "history"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please provide a date in the format YYYY-mm-dd!"));
    }

    @Test
    public void addUser() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);

        // Valid command
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Now check for correct output
        assertTrue(builtInProcess.getLogsOutput().contains("Creating directory for someUser"));

        // User already exists
        command = new String[]{"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("User someUser already exists!"));

        // Invalid number of parameters
        command = new String[]{"super", "adduser", this.username, "password"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter all parameters correctly!"));

        // Passwords don't match
        command = new String[]{"super", "adduser", this.username, "password", "password2", "standard"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Passwords do not match!"));

        // Invalid user type
        command = new String[]{"super", "adduser", this.username, "password", "password", "random"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Invalid user type"));

        // Delete user
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess.execute(command);
    }

    @Test
    public void deleteUser() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Create user to delete
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Invalid number of parameters
        command = new String[]{"super", "deluser"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter all parameters correctly!"));

        // Deleting current user
        command = new String[]{"super", "deluser", "someSuperUser"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot delete yourself!"));

        // Valid command
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Deleting user someUser"));
    }

    @Test
    public void listUsers() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Create two users to have listed
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);
        command = new String[]{"super", "adduser", "someOtherUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Valid command
        command = new String[]{"super", "listusers"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("someUser"));
        assertTrue(builtInProcess.getLogsOutput().contains("someOtherUser"));

        // Delete the two new users
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess.execute(command);
        command = new String[]{"super", "deluser", "someOtherUser"};
        builtInProcess.execute(command);
    }

    @Test
    public void chPass() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Create user to change password
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);
        // Create another user
        command = new String[]{"super", "adduser", "someSuperUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Invalid number of parameters
        command = new String[]{"super", "chpass"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter all parameters correctly!"));

        // Invalid user
        command = new String[]{"super", "chpass", "someUser2", "password", "password"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("User does not exist!"));

        // Changing password of current user
        command = new String[]{"super", "chpass", "someSuperUser", "password", "password"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot change your own password!"));

        // Valid command
        command = new String[]{"super", "chpass", "someUser", "password", "newPassword"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Password changed successfully!"));

        // Delete users
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
        command = new String[]{"super", "deluser", "someSuperUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void chUserType() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Create user to change user type
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);
        // Create another user
        command = new String[]{"super", "adduser", "someSuperUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Invalid number of parameters
        command = new String[]{"super", "chusertype"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter all parameters correctly!"));

        // Invalid user type
        command = new String[]{"super", "chusertype", "someUser", "blah"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Invalid user type!"));

        // Invalid user
        command = new String[]{"super", "chusertype", "someUser2", "standard"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("User does not exist!"));

        // Changing user type of current user
        command = new String[]{"super", "chusertype", "someSuperUser", "standard"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot change your own user type!"));

        // Valid command
        command = new String[]{"super", "chusertype", "someUser", "standard"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("User type changed!"));

        // Delete users
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
        command = new String[]{"super", "deluser", "someSuperUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void login() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Create user to login
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Already logged in
        command = new String[]{"login", "someUser", "password"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are already logged in!"));

        // Invalid number of parameters
        command = new String[]{"login"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter all parameters correctly!"));

        BuiltInProcess builtInProcess2 = new BuiltInProcess("someUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, false, this.shellType);

        // Invalid user
        command = new String[]{"login", "someUser2", "password"};
        builtInProcess2.execute(command);
        assertTrue(builtInProcess2.getLogsOutput().contains("User someUser2 does not exist!"));

        // Invalid password
        command = new String[]{"login", "someUser", "password2"};
        builtInProcess2.execute(command);
        assertTrue(builtInProcess2.getLogsOutput().contains("Invalid password."));

        // Valid command
        command = new String[]{"login", "someUser", "password"};
        builtInProcess2.execute(command);
        assertTrue(builtInProcess2.getLogsOutput().contains("Welcome someUser"));

        // Delete users
        BuiltInProcess builtInProcess3 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess3.execute(command);
    }

    @Test
    public void logout() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, false, this.shellType);
        // Create user to login
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Not logged in
        command = new String[]{"logout"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are not logged in!"));

        // Valid command
        command = new String[]{"login", "someUser", "password"};
        builtInProcess.execute(command);
        command = new String[]{"logout"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Logged out someUser"));

        // Delete users
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void whoami() {
        BuiltInProcess builtInProcess = new BuiltInProcess("someSuperUser", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, false, this.shellType);
        // Create user to login
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Not logged in
        command = new String[]{"whoami"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You are not logged in!"));

        // Valid command
        command = new String[]{"login", "someUser", "password"};
        builtInProcess.execute(command);
        command = new String[]{"whoami"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Username: someUser\nUser type: STANDARD"));

        // Delete users
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void move() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd,
                this.userType, false, this.shellType);
        // Paths are outside user's home directory
        String[] command = {"move", "..", ".."};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));
        command = new String[]{"move", "./", "somedir"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));
        command = new String[]{"move", "somedir", "../somedir"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));

        // Source file does not exist
        command = new String[]{"move", "somedir", "somedir2"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("File does not exist!"));
    }

    @Test
    public void copy() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd,
                this.userType, false, this.shellType);
        // Paths are outside user's home directory
        String[] command = {"copy", "..", ".."};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));
        command = new String[]{"copy", "./", "somedir"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));
        command = new String[]{"copy", "somedir", "../somedir"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("You cannot copy files outside of your home directory!"));

        // Source file does not exist
        command = new String[]{"copy", "somedir", "somedir2"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("File does not exist!"));
    }

    @Test
    public void cd() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, false, this.shellType);
        // Create user to login
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Log in as user
        command = new String[]{"login", "someUser", "password"};
        builtInProcess.execute(command);

        // Make a new directory inside user's home directory
        File newDir = new File(this.cwd + "/someUser/somedir");
        newDir.mkdir();

        // cd command with invalid path
        command = new String[]{"cd", "dirdoesntexist"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please enter a valid path! You cannot exit your local area!"));

        // cd command with valid path
        command = new String[]{"cd", "somedir"};
        builtInProcess.execute(command);
        assertEquals(builtInProcess.getCWD(), this.cwd + "someUser/somedir");

        // Just cd command on its own
        command = new String[]{"cd"};
        builtInProcess.execute(command);
        assertEquals(builtInProcess.getCWD(), this.cwd + "someUser");

        // Log out
        command = new String[]{"logout"};
        builtInProcess.execute(command);

        // Delete user
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void showDir() {
        BuiltInProcess builtInProcess = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, false, this.shellType);
        // Create user to login
        String[] command = {"super", "adduser", "someUser", "password", "password", "standard"};
        builtInProcess.execute(command);

        // Log in as user
        command = new String[]{"login", "someUser", "password"};
        builtInProcess.execute(command);

        // Valid command
        command = new String[]{"showdir"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("someUser"));

        // Log out
        command = new String[]{"logout"};
        builtInProcess.execute(command);

        // Delete user
        BuiltInProcess builtInProcess2 = new BuiltInProcess(this.username, this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        command = new String[]{"super", "deluser", "someUser"};
        builtInProcess2.execute(command);
    }

    @Test
    public void history() {
        BuiltInProcess builtInProcess = new BuiltInProcess("admin", this.cwd,
                BuiltIns.UserTypes.SUPERUSER, true, this.shellType);
        // Input invalid date
        String[] command = {"super", "history", "invalid"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("Please input a valid date!"));

        // Input valid date format but has no file
        command = new String[]{"super", "history", "2019-01-01"};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("There are no log files with this date!"));

        // Input valid date format and has a file
        // Get current YYYY-MM-DD
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String date = dtf.format(localDate);
        command = new String[]{"super", "history", date};
        builtInProcess.execute(command);
        assertTrue(builtInProcess.getLogsOutput().contains("History for " + date + ":"));
    }
}