package ss.shell.tests;

import org.junit.Before;
import ss.shell.BuiltInProcess;
import ss.shell.utils.BuiltIns;

import org.junit.Test;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

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
}