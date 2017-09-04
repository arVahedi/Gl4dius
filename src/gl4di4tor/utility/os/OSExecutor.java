package gl4di4tor.utility.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gladiator on 9/4/17.
 */
public class OSExecutor {

    public static String execute(String command) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        // read the output from the command
        StringBuilder output = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            output.append(s);
        }

        return output.toString();
    }
}
