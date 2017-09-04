package gl4di4tor.utility.os;

/**
 * Created by gladiator on 9/3/17.
 */
public class OSDetector {
    private static String operationSystem = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (operationSystem.contains("win"));
    }

    public static boolean isMac() {
        return (operationSystem.contains("mac"));
    }

    public static boolean isUnix() {
        return (operationSystem.contains("nix") ||
                operationSystem.contains("nux") ||
                operationSystem.contains("aix"));
    }

    public static boolean isSolaris() {
        return (operationSystem.contains("sunos"));
    }

}
