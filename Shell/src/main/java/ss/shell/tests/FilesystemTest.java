package ss.shell.tests;

import org.junit.Before;
import org.junit.Test;
import ss.shell.utils.BuiltIns;
import ss.shell.utils.Filesystem;

import java.io.File;

import static junit.framework.Assert.*;

public class FilesystemTest {
    private String username;
    private String password;
    private BuiltIns.ShellType shellType;

    public FilesystemTest() {
    }

    @Before
    public void setUp() {
        this.username = "new_user";
        this.password = "new_user_pass";
        this.shellType = BuiltIns.ShellType.REMOTE;
    }

    @Test
    public void login() {
        // Create a new user that does not have a directory yet
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertTrue(fs.login());

        // Login the same user with a different password
        Filesystem fs2 = new Filesystem(this.username, String.valueOf(this.password.hashCode() + 1),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        assertFalse(fs2.login());
        assertTrue(fs2.getLogsOutput().contains("Invalid password."));

        // Delete the user
        fs.deleteUser();
        assertFalse(fs.login());
        assertTrue(fs.getLogsOutput().contains("User " + this.username + " does not exist!"));
    }

    @Test
    public void createUser() {
        // Create a new user that does not have a directory yet
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertTrue(fs.getLogsOutput().contains("Creating directory for " + this.username)
                && fs.getLogsOutput().contains("Wrote user details to " + BuiltIns.HOME_PATH + this.username + ".txt"));
        assertTrue(fs.hasDirectory());

        // Delete the user
        fs.deleteUser();
    }

    @Test
    public void deleteUser() {
        // Create a new user that does not have a directory yet
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();

        // Delete the user
        fs.deleteUser();
        assertTrue(fs.getLogsOutput().contains("Deleting user " + this.username));
        assertFalse(fs.hasDirectory());

        // Try deleting the user again
        fs.deleteUser();
        assertTrue(fs.getLogsOutput().contains("User " + this.username + " does not exist!"));
    }

    @Test
    public void getPassword() {
        // Create a new user that does not have a directory yet and getting its password hash
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertEquals(String.valueOf(this.password.hashCode()), fs.getPassword());

        // Delete the user
        fs.deleteUser();

        // Try getting the password of a user that does not exist
        assertEquals("", fs.getPassword());

    }

    @Test
    public void getUserType() {
        // Create a new user that does not have a directory yet and getting its user type
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertEquals(BuiltIns.UserTypes.valueOf("SUPERUSER"), fs.getUserType());

        // Delete the user
        fs.deleteUser();

        // Try getting the user type of user that does not exist
        assertNull(fs.getUserType());
    }

    @Test
    public void changePassword() {
        // Create a new user that does not have a directory yet and changing its password
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertTrue(fs.changePassword(String.valueOf(this.password.hashCode() + 1)));
        assertEquals(String.valueOf(this.password.hashCode() + 1), fs.getPassword());

        // Delete the user
        fs.deleteUser();

        // Try changing the password of a user that does not exist
        fs.changePassword(String.valueOf(this.password.hashCode()));
        assertTrue(fs.getLogsOutput().contains("User " + this.username + " does not exist!"));
    }

    @Test
    public void changeUserType() {
        // Create a new user that does not have a directory yet and changing its user type
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertTrue(fs.changeUserType(BuiltIns.UserTypes.valueOf("STANDARD")));
        assertEquals(BuiltIns.UserTypes.valueOf("STANDARD"), fs.getUserType());

        // Delete the user
        fs.deleteUser();

        // Try changing the user type of user that does not exist
        fs.changeUserType(BuiltIns.UserTypes.valueOf("SUPERUSER"));
        assertTrue(fs.getLogsOutput().contains("User " + this.username + " does not exist!"));
    }

    @Test
    public void hasDirectory() {
        // Create a new user that does not have a directory yet
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertTrue(fs.hasDirectory());

        // Delete the user
        fs.deleteUser();

        // Try checking if the user has a directory
        assertFalse(fs.hasDirectory());
    }

    @Test
    public void getUserFiles() {
        // Get the number of current users
        int numberOfUsers = Filesystem.getUserFiles().length;

        // Create a new user that does not have a directory yet
        Filesystem fs = new Filesystem(this.username, String.valueOf(this.password.hashCode()),
                BuiltIns.UserTypes.valueOf("SUPERUSER"), shellType);
        fs.createUser();
        assertEquals(numberOfUsers + 1, Filesystem.getUserFiles().length);

        // Delete the user
        fs.deleteUser();

        // Try getting the user files of user that does not exist
        assertEquals(numberOfUsers, Filesystem.getUserFiles().length);
    }

    @Test
    public void listUsers() {
        // Create two directories inside the home path
        new File(BuiltIns.HOME_PATH + this.username).mkdir();
        new File(BuiltIns.HOME_PATH + this.username + "2").mkdir();

        Filesystem fs = new Filesystem(BuiltIns.HOME_PATH);
        File path = new File(BuiltIns.HOME_PATH);
        File[] files = path.listFiles();

        // Delete the directories
        new File(BuiltIns.HOME_PATH + this.username).delete();
        new File(BuiltIns.HOME_PATH + this.username + "2").delete();
    }
}