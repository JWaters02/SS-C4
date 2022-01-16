package ss.shell.utils;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.io.IOException;

public class Logs {
    /**
     * Prints a line of text and stores output in the logs file.
     * @param text Input text.
     */
    public static void printLine(String text) {
        System.out.println(text);
        logToFile(text);
    }

    /**
     * Prints a line of text and stores output in the logs file.
     * @param text Input text.
     */
    public static void print(String text) {
        System.out.print(text);
        logToFile(text);
    }

    /**
     * Stores text in the logs file with a date and time stamp.
     * @param text Input text.
     */
    private static void logToFile(String text) {
        try {
            // We want logs file to be stored in the home directory (Pedro)
            File logsFile = new File(System.getProperty("user.home") + File.separator + "logs.txt");
            FileWriter fileWriter = new FileWriter(logsFile, true);
            // Write [date] [time]: [text]
            fileWriter.write("[" + LocalDateTime.now().toString() + "]: " + text);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
