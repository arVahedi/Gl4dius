package gl4di4tor.utility.security;

import gl4di4tor.utility.os.OSExecutor;

import java.io.IOException;

/**
 * Created by gladiator on 9/3/17.
 */
public class Authorization {

    public static boolean isRootUser() throws IOException, InterruptedException {
        return Integer.valueOf(OSExecutor.execute("id -u")) == 0;
    }
}
