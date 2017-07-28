package gl4di4tor.log;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.assets.LogLevel;
import gl4di4tor.configuration.Config;

import java.io.IOException;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LogService getInstance() {
        return instance;
    }

    public static void debug(Object message) {
        instance.printDate(LogLevel.DEBUG);
        instance.coloredPrinter.debugPrintln(message, LogLevel.DEBUG.getValue());
        instance.coloredPrinter.clear();
    }

    public static void info(Object message) {
        instance.printDate(LogLevel.INFO);
        instance.coloredPrinter.debugPrintln(message, LogLevel.INFO.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.BLUE, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public static void warning(Object message) {
        instance.printDate(LogLevel.WARNING);
        instance.coloredPrinter.debugPrintln(message, LogLevel.WARNING.getValue(), Ansi.Attribute.NONE, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public static void error(Object message) {
        instance.printDate(LogLevel.ERROR);
        instance.coloredPrinter.debugPrintln(message, LogLevel.ERROR.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        instance.coloredPrinter.clear();
    }

    public static void fatal(Object message) {
        instance.printDate(LogLevel.FATAL);
        instance.coloredPrinter.debugPrintln(message, LogLevel.FATAL.getValue(), Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.YELLOW);
        instance.coloredPrinter.clear();
    }

    private void printDate(LogLevel level) {
        instance.coloredPrinter.debugPrint(instance.coloredPrinter.getDateFormatted(), level.getValue(), Ansi.Attribute.NONE, Ansi.FColor.CYAN, Ansi.BColor.BLACK);
        instance.coloredPrinter.clear();
        instance.coloredPrinter.debugPrint(" - ", level.getValue());
    }
}
