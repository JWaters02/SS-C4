package ss.shell.utils;

public class ShellUtils {
    /**
     * @param seconds The number of seconds to sleep.
     */
    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
