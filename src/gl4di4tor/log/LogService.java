package gl4di4tor.log;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.assets.LogLevel;
import gl4di4tor.configuration.Config;

/**
 * Created by Gladiator on 7/28/2017 AD.
 */
public class LogService {
    private ColoredPrinter coloredPrinter;

    private static LogService instance;

    static {
        instance = new LogService();
    }

    private LogService() {
        try {
            this.coloredPrinter = new ColoredPrinter.Builder(Config.getInstance().getLogLevel(), false)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LogService(int level) {
        try {
            this.coloredPrinter = new ColoredPrinter.Builder(level, false)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static LogService getInstance() {
        return instance;
    }

    public synchronized static void debug(Object message) {
        instance.printDate(LogLevel.DEBUG);
        instance.coloredPrinter.debugPrintln("[DEBUG] : " + message, LogLevel.DEBUG.getValue());
        instance.coloredPrinter.clear();
    }

    public synchronized static void info(Object message) {
        instance.printDate(LogLevel.INFO);
        instance.coloredPrinter.debugPrintln("[INFO] : " + message, LogLevel.INFO.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public synchronized static void warning(Object message) {
        instance.printDate(LogLevel.WARNING);
        instance.coloredPrinter.debugPrintln("[WARNING] : " + message, LogLevel.WARNING.getValue(), Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public synchronized static void error(Object message) {
        instance.printDate(LogLevel.ERROR);
        instance.coloredPrinter.debugPrintln("[ERROR] : " + message, LogLevel.ERROR.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public synchronized static void fatal(Object message) {
        instance.printDate(LogLevel.FATAL);
        instance.coloredPrinter.debugPrintln("[FATAL] : " + message, LogLevel.FATAL.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.YELLOW);
        instance.coloredPrinter.clear();
    }

    public synchronized static void fatal(Object message, boolean useConfig) {
        if (!useConfig) {
            ColoredPrinter coloredPrinter = new ColoredPrinter.Builder(LogLevel.FATAL.getValue(), false).build();
            coloredPrinter.debugPrint(coloredPrinter.getDateFormatted(), Ansi.Attribute.NONE, Ansi.FColor.CYAN, Ansi.BColor.BLACK);
            coloredPrinter.clear();
            coloredPrinter.debugPrint(" - ");
            coloredPrinter.debugPrintln("[FATAL] : " + message, LogLevel.FATAL.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.YELLOW);
            coloredPrinter.clear();
        } else {
            fatal(message);
        }
    }

    public synchronized static void log(Object message, Ansi.FColor fColor, Ansi.BColor bColor) {
        instance.printDate(LogLevel.PERSIST);
        instance.coloredPrinter.debugPrintln(message, LogLevel.PERSIST.getValue(), Ansi.Attribute.BOLD, fColor, bColor);
        instance.coloredPrinter.clear();
    }

    public synchronized static void log(Object message) {
        instance.printDate(LogLevel.PERSIST);
        instance.coloredPrinter.debugPrintln(message, LogLevel.PERSIST.getValue(), Ansi.Attribute.NONE, Ansi.FColor.YELLOW,
                Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    private synchronized void printDate(LogLevel level) {
        instance.coloredPrinter.debugPrint(instance.coloredPrinter.getDateFormatted(), level.getValue(), Ansi.Attribute.NONE, Ansi.FColor.CYAN, Ansi.BColor.BLACK);
        instance.coloredPrinter.clear();
        instance.coloredPrinter.debugPrint(" - ", level.getValue());
    }
}
