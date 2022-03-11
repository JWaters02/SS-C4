package ss.shell.tests;

import org.junit.jupiter.api.Test;
import ss.shell.utils.BuiltIns;
import ss.shell.utils.Logs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LogsTest {
    @Test
    void outputPrompt() {
        System.out.println("Output Prompt");
        Logs logs = new Logs();
        logs.outputPrompt("test prompt", Logs.Store.NO);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-path\">test prompt</span>\n" +
                "<span class=\"terminal_prompt-end\">:~$</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    void outputShort() {
        System.out.println("Output Short");
        Logs logs = new Logs();
        logs.outputShort("some output!", Logs.Store.NO);
        String output = "\n" + logs.getOuterTop() + "<span class=\"terminal_prompt-short\">some output!</span>" +
                logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    void outputInfo_Error() {
        System.out.println("Output Info Error");
        Logs logs = new Logs();
        logs.outputInfo("this is an error!", Logs.Store.NO, Logs.LogLevel.ERROR);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-error\"> Error: this is an error!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }


    @Test
    void outputInfo_Warning() {
        System.out.println("Output Info Warning");
        Logs logs = new Logs();
        logs.outputInfo("this is a warning!", Logs.Store.NO, Logs.LogLevel.WARNING);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-warning\"> Warning: this is a warning!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }


    @Test
    void outputInfo_Info() {
        System.out.println("Output Info Info");
        Logs logs = new Logs();
        logs.outputInfo("this is info!", Logs.Store.NO, Logs.LogLevel.WARNING);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-info\"> Info: this is info!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }


    @Test
    void getOuterTop() {
        System.out.println("Outer Top");
        Logs logs = new Logs();
        assertEquals("<div class=\"terminal_prompt\">\n", logs.getOuterTop());
    }


    @Test
    void getOuterBottom() {
        System.out.println("Outer Bottom");
        Logs logs = new Logs();
        assertEquals("\n</div>", logs.getOuterTop());
    }

    @Test
    void addToOutputTotal() {
        System.out.println("Add To Output Total");
        Logs logs = new Logs();
        logs.addToOutputTotal("test");
        assertEquals("test", logs.getOutputTotal());

        logs.addToOutputTotal("tests 2 and 3");
        assertEquals("test tests 2 and 3", logs.getOutputTotal());
    }

    @Test
    void getTwoDigits() {
        System.out.print("Get Two Digits");
        assertEquals("00", Logs.getTwoDigits(0));
        assertEquals("01", Logs.getTwoDigits(1));
        assertEquals("10", Logs.getTwoDigits(10));
    }
}