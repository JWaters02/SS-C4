package ss.shell.tests;

import org.junit.Test;
import ss.shell.utils.BuiltIns;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class BuiltInsTest {
    public BuiltInsTest() {
    }

    @Test
    public void isBuiltIn() {
        assertFalse(BuiltIns.isBuiltIn("not a real command"));
        assertFalse(BuiltIns.isBuiltIn("exit"));
        assertFalse(BuiltIns.isBuiltIn("pwd"));
        assertFalse(BuiltIns.isBuiltIn("cp"));

        assertTrue(BuiltIns.isBuiltIn("super"));
        assertTrue(BuiltIns.isBuiltIn("cd"));
        assertTrue(BuiltIns.isBuiltIn("login"));
    }

    @Test
    public void isProcessBuilder() {
        assertFalse(BuiltIns.isProcessBuilder("not a real command"));
        assertFalse(BuiltIns.isProcessBuilder("exit"));
        assertFalse(BuiltIns.isProcessBuilder("super"));
        assertFalse(BuiltIns.isProcessBuilder("cd"));
        assertFalse(BuiltIns.isProcessBuilder("login"));

        assertTrue(BuiltIns.isProcessBuilder("pwd"));
        assertTrue(BuiltIns.isProcessBuilder("cp"));
        assertTrue(BuiltIns.isProcessBuilder("ls"));
    }
}