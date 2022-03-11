package ss.shell.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ss.shell.BuiltInProcess;
import ss.shell.utils.BuiltIns;

import static org.junit.jupiter.api.Assertions.*;

class BuiltInProcessTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void getUsername() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);
        assertEquals(username, builtInProcess.getUsername());
    }

    @Test
    void getCWD() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);
        assertEquals(cwd, builtInProcess.getCWD());
    }

    @Test
    void getUserType() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);
        assertEquals(userType, builtInProcess.getUserType());
    }

    @Test
    void getIsLoggedIn() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);
        assertEquals(isLoggedIn, builtInProcess.getIsLoggedIn());
    }

    @Test
    void getLogsOutput() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);
        assertEquals("", builtInProcess.getLogsOutput());
    }

    @Test
    void execute() {
        String username = "guest";
        String cwd = BuiltIns.HOME_PATH;
        BuiltIns.UserTypes userType = BuiltIns.UserTypes.STANDARD;
        boolean isLoggedIn = false;
        BuiltIns.ShellType shellType = BuiltIns.ShellType.REMOTE;
        BuiltInProcess builtInProcess = new BuiltInProcess(username, cwd, userType, isLoggedIn, shellType);

        String[] command = {"super"};
        builtInProcess.execute(command);
        assertEquals("Please specify a command", builtInProcess.getLogsOutput());

    }
}