package gl4di4tor;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import gl4di4tor.configuration.Config;
import gl4di4tor.engine.proxy.ProxyEngine;

import java.io.IOException;

public class Core {

    public static void main(String[] args) throws IOException {
        try {
            Thread t = new ProxyEngine(Config.getInstance().getProxyServerPort());
            t.start();


            ///////////////////////////////////////
            ColoredPrinter cp = new ColoredPrinter.Builder(1, false)
                    .foreground(Ansi.FColor.WHITE).background(Ansi.BColor.BLUE)   //setting format
                    .build();

            //printing according to that format
            cp.println(cp);
            cp.setAttribute(Ansi.Attribute.REVERSE);
            cp.println("This is a normal message (with format reversed).");

            //resetting the terminal to its default colors
            cp.clear();
            cp.print(cp.getDateFormatted(), Ansi.Attribute.NONE, Ansi.FColor.CYAN, Ansi.BColor.BLACK);
            cp.debugPrintln(" This debug message is always printed.");
            cp.clear();
            cp.print("This example used JCDP 1.25   ");

            //temporarily overriding that format
            cp.print("\tMADE ", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.GREEN);
            cp.print("IN PORTUGAL\t\n", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
            cp.println("I hope you find it useful ;)");

            cp.clear(); //don't forget to clear the terminal's format before exiting
            ///////////////////////////////////////


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}