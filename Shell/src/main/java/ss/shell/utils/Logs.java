package ss.shell.utils;

import java.io.File;
import java.io.FileWriter;
import java.time.*;
import java.io.IOException;

public class Logs {
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }

    public enum Store {
        YES,
        NO
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     */
    public static void printLine(String text) {
        System.out.println(text);
        logToFile(text);
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     * @param store An override param to not store output in logs file.
     */
    public static void printLine(String text, Store store) {
        if (store == Store.NO) { System.out.println(text); }
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     * @param level Log level.
     */
    public static void printLine(String text, LogLevel level) {
        levelPrints(text, level);
        logToFile(text);
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     * @param level Log level.
     * @param store An override param to not store output in logs file.
     */
    public static void printLine(String text, LogLevel level, Store store) {
        if (store == Store.NO) { levelPrints(text, level); }
    }

    /**
     * Prints a line of text and stores output in the logs file.
     * @param text Input text.
     * @param store Store output in logs file.
     */
    public static void print(String text, Store store) {
        System.out.print(text);
        if (store == Store.YES) {
            logToFile(text);
        }
    }

    private static void levelPrints(String text, LogLevel level) {
        if (level == LogLevel.ERROR) {
            System.out.println(ConsoleColours.RED + "Error: " + text + ConsoleColours.RESET);
        } else if (level == LogLevel.WARNING) {
            System.out.println(ConsoleColours.YELLOW + "Warning: " + text + ConsoleColours.RESET);
        } else if (level == LogLevel.INFO) {
            System.out.println(ConsoleColours.CYAN + "Info: " + text + ConsoleColours.RESET);
        }
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
            fileWriter.write("[" + getDate() + " " + getTime() + "]: " + text + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores text in the logs file with a date and time stamp.
     * @param text Input text.
     */
    public static void logToFile(String[] text) {
        final String fileName = BuiltIns.HOME_PATH + LocalDate.now() + "_log.txt";
        try {
            // We want logs file to be stored in the home directory
            File logsFile = new File(fileName);
            FileWriter fileWriter = new FileWriter(logsFile, true);
            for (String line : text) {
                // Write [date] [time]: [text]
                fileWriter.write("[" + getDate() + " " + getTime() + "]: " + line + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current date.
     * @return Current date, in format yyyy-mm-dd.
     */
    private static String getDate() {
        return LocalDate.now().toString();
    }

    /**
     * Returns the current time.
     * @return Current time, in format hh:mm:ss.
     */
    private static String getTime() {
        return getTwoDigits(LocalTime.now().getHour()) + ":" + getTwoDigits(LocalTime.now().getMinute()) +
                ":" + getTwoDigits(LocalTime.now().getSecond());
    }

    /**
     * Returns the full 2 digits of input string.
     * @param input Input int.
     * @return Input string with 2 digits.
     */
    private static String getTwoDigits(int input) {
        String inputString = Integer.toString(input);
        if (inputString.length() == 1) {
            return "0" + inputString;
        } else {
            return inputString;
        }
    }
}
