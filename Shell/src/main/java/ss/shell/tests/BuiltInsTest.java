package ss.shell.tests;

import org.junit.jupiter.api.Test;
import ss.shell.utils.BuiltIns;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuiltInsTest {
    @Test
    void isBuiltIn() {
        System.out.println("isBuiltIn");
        assertFalse(BuiltIns.isBuiltIn("not a real command"));
        assertFalse(BuiltIns.isBuiltIn("exit"));
        assertFalse(BuiltIns.isBuiltIn("pwd"));
        assertFalse(BuiltIns.isBuiltIn("cp"));

        assertTrue(BuiltIns.isBuiltIn("super"));
        assertTrue(BuiltIns.isBuiltIn("cd"));
        assertTrue(BuiltIns.isBuiltIn("login"));
    }

    @Test
    void isProcessBuilder() {
        System.out.println("isProcessBuilder");
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