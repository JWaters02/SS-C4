package ss.shell;

import ss.shell.utils.BuiltIns;

import java.io.File;

public class ShellProcess {
    /**
     * Use ProcessBuilder to simulate a shell process.
     * @param commandTokens The command to execute with any flags and arguments.
     * @return the output of the command
     */
    public String execute(String[] commandTokens) {
        String output;
        try {
            ProcessBuilder pb = new ProcessBuilder(commandTokens);
            pb.directory(new File(BuiltIns.HOME_PATH));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            output = getOutput(p);
        } catch (Exception e) {
            output = e.getMessage();
        }
        return output;
    }

    private String getOutput(Process p) throws Exception {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = p.getInputStream().read()) != -1) {
            sb.append((char)c);
        }
        return sb.toString();
    }
}
