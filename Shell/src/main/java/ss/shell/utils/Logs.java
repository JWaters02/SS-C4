package ss.shell.utils;

import java.io.File;
import java.io.FileWriter;
import java.time.*;
import java.io.IOException;

public class Logs {
    private String outputTotal = "";

    /**
     * The severity level of the logs.
     */
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR;
    }

    /**
     * Whether to store the logged output to the logs file.
     */
    public enum Store {
        YES,
        NO
    }

    /**
     * Add the HTML prompt to the total output.
     * @param prompt Prompt to output.
     * @param store Whether to store the output to logs.
     */
    public void outputPrompt(String prompt, Store store) {
        if (store == Store.YES) {
            logToFile(prompt);
        }
        this.outputTotal += getOuterTop();
        this.outputTotal += "<span class=\"terminal_prompt-path\">" + prompt + "</span>\n" +
                            "<span class=\"terminal_prompt-end\">:~$</span>";
        this.outputTotal += getOuterBottom();
    }

    /**
     * Add the HTML short to the total output.
     * @param output Output.
     * @param store Whether to store the output to logs.
     */
    public void outputShort(String output, Store store) {
        if (store == Store.YES) {
            logToFile(output);
        }
        String[] lines = output.split("\n");
        for (String line : lines) {
            this.outputTotal += "\n";
            this.outputTotal += getOuterTop();
            this.outputTotal += "<span class=\"terminal_prompt-short\">" + line + "</span>";
            this.outputTotal += getOuterBottom();
        }
    }

    /**
     * Add the HTML info to the total output.
     * @param output Output.
     * @param store Whether to store the output to logs.
     */
    public void outputInfo(String output, Store store, LogLevel level) {
        if (store == Store.YES) {
            logToFile(output);
        }
        this.outputTotal += getOuterTop();
        if (level == LogLevel.ERROR) {
            this.outputTotal += "<span class=\"terminal_prompt-error\">" + output + "</span>";
        } else if (level == LogLevel.WARNING) {
            this.outputTotal += "<span class=\"terminal_prompt-warning\">" + output + "</span>";
        } else if (level == LogLevel.INFO) {
            this.outputTotal += "<span class=\"terminal_prompt-info\">" + output + "</span>";
        }
        this.outputTotal += getOuterBottom();
    }

    /**
     * Get the top of the HTML string.
     * @return The top of the HTML string.
     */
    private String getOuterTop() {
        return "<div class=\"terminal_prompt\">\n";
    }

    /**
     * Get the bottom of the HTML string.
     * @return The bottom of the HTML string.
     */
    private String getOuterBottom() {
        return "\n</div>";
    }

    /**
     * Get the total output HTML.
     * @return The whole HTML string to output.
     */
    public String getOutputTotal() { return this.outputTotal; }

    /**
     * Directly add extra text to the total output string.
     * @param output Text to add.
     */
    public void addToOutputTotal(String output) {
        this.outputTotal += output;
    }

    /**
     * Prints a line (with \n) of text and stores output in the logs file.
     * @param text Input text.
     * @param store Tell it not to store output in logs file.
     */
    public static void printLine(String text, Store store) {
        System.out.println(text);
        if (store == Store.YES) { logToFile(text); }
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

    /**
     * Print the line of text according to the level severity.
     * @param text Input text.
     * @param level Severity of output (by colour).
     */
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
    public static void logToFile(String text) {
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
