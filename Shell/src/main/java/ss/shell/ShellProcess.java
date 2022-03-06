package ss.shell;

import java.io.File;

public record ShellProcess(String cwd) {
    /**
     * Constructor for this record.
     * @param cwd The current working directory.
     */
    public ShellProcess {
    }

    /**
     * Use ProcessBuilder to simulate a shell process.
     *
     * @param commandTokens The command to execute with any flags and arguments.
     * @return the output of the command
     */
    public String execute(String[] commandTokens) {
        String output;
        try {
            ProcessBuilder pb = new ProcessBuilder(commandTokens);
            pb.directory(new File(this.cwd));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            output = getOutput(p);
        } catch (Exception e) {
            output = e.getMessage();
        }
        return output;
    }

    /**
     * Get the output of the process builder command.
     *
     * @param p Process to get output from.
     * @return The output string.
     */
    private String getOutput(Process p) throws Exception {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = p.getInputStream().read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }
}
