package ss.shell.tests;

import ss.shell.utils.Logs;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class LogsTest {
    public LogsTest() {
    }

    @Test
    public void outputPrompt() {
        Logs logs = new Logs();
        logs.outputPrompt("test prompt", Logs.Store.NO);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-path\">test prompt</span>\n" +
                "<span class=\"terminal_prompt-end\">:~$</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    public void outputShort() {
        Logs logs = new Logs();
        logs.outputShort("some output!", Logs.Store.NO);
        String output = "\n" + logs.getOuterTop() + "<span class=\"terminal_prompt-short\">some output!</span>" +
                logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    public void outputInfo_Error() {
        Logs logs = new Logs();
        logs.outputInfo("this is an error!", Logs.Store.NO, Logs.LogLevel.ERROR);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-error\"> Error: this is an error!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    public void outputInfo_Warning() {
        Logs logs = new Logs();
        logs.outputInfo("this is a warning!", Logs.Store.NO, Logs.LogLevel.WARNING);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-warning\"> Warning: this is a warning!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    public void outputInfo_Info() {
        Logs logs = new Logs();
        logs.outputInfo("this is info!", Logs.Store.NO, Logs.LogLevel.INFO);
        String output = logs.getOuterTop() + "<span class=\"terminal_prompt-info\"> Info: this is info!" +
                "</span>" + logs.getOuterBottom();
        assertEquals(output, logs.getOutputTotal());
    }

    @Test
    public void getOuterTop() {
        Logs logs = new Logs();
        assertEquals("<div class=\"terminal_prompt\">\n", logs.getOuterTop());
    }

    @Test
    public void getOuterBottom() {
        Logs logs = new Logs();
        assertEquals("\n</div>", logs.getOuterBottom());
    }

    @Test
    public void addToOutputTotal() {
        Logs logs = new Logs();
        logs.addToOutputTotal("test");
        assertEquals("test", logs.getOutputTotal());

        logs.addToOutputTotal(" tests 2 and 3");
        assertEquals("test tests 2 and 3", logs.getOutputTotal());
    }

    @Test
    public void getTwoDigits() {
        assertEquals("00", Logs.getTwoDigits(0));
        assertEquals("01", Logs.getTwoDigits(1));
        assertEquals("10", Logs.getTwoDigits(10));
    }
}