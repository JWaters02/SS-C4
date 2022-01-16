package ss.shell.utils;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.IOException;

public class Logs {
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     */
    public static void printLine(String text) {
        System.out.println(text);
        logToFile(text);
    }

    public static void printLine(String text, LogLevel level) {
        if (level == LogLevel.ERROR) {
            System.out.println(ConsoleColours.RED + "Error: " + text + ConsoleColours.RESET);
        } else if (level == LogLevel.WARNING) {
            System.out.println(ConsoleColours.YELLOW + "Warning: " + text + ConsoleColours.RESET);
        } else if (level == LogLevel.INFO) {
            System.out.println(ConsoleColours.CYAN + "Info: " + text + ConsoleColours.RESET);
        }
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
        final String fileName = BuiltIns.HOME_PATH + LocalDate.now() + "_log.txt";
        try {
            // We want logs file to be stored in the home directory
            File logsFile = new File(fileName);
            FileWriter fileWriter = new FileWriter(logsFile, true);
            // Write [date] [time]: [text]
            fileWriter.write("[" + LocalDateTime.now().toString() + "]: " + text + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
