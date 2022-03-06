package ss.shell;

import ss.shell.utils.BuiltIns;
import ss.shell.utils.BuiltIns.*;
import ss.shell.utils.Logs;
import ss.shell.utils.Logs.*;

public class Shell {
    private final String input;
    private final Logs logs;
    private boolean doExit = false;
    private boolean isLoggedIn;
    private String username;
    private String cwd;
    private UserTypes type;

    public Shell(String input, String username, String cwd, UserTypes type, boolean isLoggedIn) {
        this.input = input;
        this.username = username;
        this.cwd = cwd;
        this.type = type;
        this.isLoggedIn = isLoggedIn;
        this.logs = new Logs();
        prompt();
    }

    public void prompt() {
        // Initialise the correct process
        BuiltInProcess bip;
        bip = new BuiltInProcess(this.username, this.cwd, this.type, this.isLoggedIn, ShellType.REMOTE);

        if (this.input.length() == 0) {
            this.logs.outputInfo("No command entered", Store.NO, LogLevel.ERROR);
        } else {
            // Output input to log for history purposes
            Logs.logToFile(this.input);

            // Parse user input
            String[] command = this.input.split(" ");
            if (isExit(command)) {
                this.doExit = true;
                this.logs.outputInfo("Exiting remote shell!", Store.NO, LogLevel.WARNING);
            } else {
                // If the command is a built-in command, run it
                if (isBuiltInCommand(command[0])) {
                    Logs.logToFile(command);
                    bip.execute(command);

                    // Get all the info again
                    this.username = bip.getUsername();
                    this.cwd = bip.getCWD();
                    this.type = bip.getUserType();
                    this.isLoggedIn = bip.getIsLoggedIn();
                    this.logs.addToOutputTotal(bip.getLogsOutput());

                    // Add the new prompt to the output
                    this.logs.outputPrompt(this.cwd, Store.YES);
                } else {
                    // If it's a process builder command
                    if (isAllowedCommand(command[0])) {
                        Logs.logToFile(command);
                        // If the PB command is valid
                        if (validatePBCommand(command)) {
                            ShellProcess process = new ShellProcess(bip.getCWD());
                            String output = process.execute(command);
                            this.logs.outputShort(output, Store.NO);

                            // Add the new prompt to the output
                            this.logs.outputPrompt(this.cwd, Store.YES);
                        } else {
                            this.logs.outputInfo("Invalid process builder command!", Store.NO, LogLevel.ERROR);
                        }
                    } else {
                        this.logs.outputInfo("Command does not exist or is not allowed.", Store.NO, LogLevel.WARNING);
                    }
                }
            }
        }
    }

    /**
     * Gets the exit status.
     * @return if the user has entered an exit command.
     */
    public boolean getDoExit() { return this.doExit; }

    /**
     * Gets the username of the current user.
     * @return username of the current user.
     */
    public String getUsername() {  return this.username; }

    /**
     * Gets the cwd of the current user.
     * @return cwd of the current user.
     */
    public String getCWD() { return this.cwd; }

    /**
     * Gets the user type of the current user.
     * @return user type of the current user.
     */
    public UserTypes getUserType() { return this.type; }

    /**
     * Gets the boolean of if the user is logged in or not.
     * @return the boolean of if the user is logged in or not.
     */
    public boolean getIsLoggedIn() { return this.isLoggedIn; }

    /**
     * Gets the total output of all the html from the logs object.
     * @return the output html.
     */
    public String getOutput() {
        if (this.logs.getOutputTotal().isEmpty()) {
            // Output a default prompt
            this.logs.outputPrompt(this.cwd, Store.YES);
        }
        return this.logs.getOutputTotal();
    }

    /**
     * Check if the command is not a shell command, but a built-in one.
     * @param command The command to check.
     * @return True if the command is a built-in command, false otherwise.
     */
    private boolean isBuiltInCommand(String command) {
        return BuiltIns.isBuiltIn(command.toLowerCase());
    }

    /**
     * Check if the command is allowed to be executed with process builder.
     * @param command The command to check.
     * @return If the command is not allowed, return false.
     */
    private boolean isAllowedCommand(String command) {
        return BuiltIns.isProcessBuilder(command.toLowerCase());
    }

    /**
     * Check if the command is to do with exiting the shell.
     * List of exit commands:
     * exit, quit, bye
     * @param command The command to check.
     * @return True if the command is an exit command, false otherwise.
     */
    private boolean isExit(String[] command) {
        return command[0].equals("exit") || command[0].equals("quit") || command[0].equals("bye");
    }

    private boolean validatePBCommand(String[] command) {
        // TODO: Implement
        return true;
    }
}
