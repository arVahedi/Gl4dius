package gl4di4tor.utility.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by gladiator on 9/3/17.
 */
public class Authorization {

    public static boolean isRootUser() throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec("id -u");
        proc.waitFor();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        // read the output from the command
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            sb.append(s);
        }

        return Integer.valueOf(sb.toString()) == 0;
    }
}
