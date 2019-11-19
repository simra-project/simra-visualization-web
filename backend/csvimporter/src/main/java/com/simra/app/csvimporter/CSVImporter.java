package main.java.com.simra.app.csvimporter;

import main.java.com.simra.app.csvimporter.handler.ProfileFileIOHandler;
import main.java.com.simra.app.csvimporter.handler.RideFileIOHandler;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.nio.file.Path;
import java.nio.file.Paths;


public class CSVImporter {

    private static Float DEFAULT_COORDINATE_MIN_ACCURACY = 20f;
    private static Double DEFAULT_RDP_EPSILON = 0.0000001;

    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("CSVImporter").build()
                .defaultHelp(true)
                .description("Import CSV file.");
        parser.addArgument("-t", "--type")
                .choices("r", "p").setDefault("r")
                .help("Specify file type r <- ride, p <- profile");
        parser.addArgument("file").nargs("*")
                .help("File to read");
        parser.addArgument("-a", "--accuracy")
                .help("Minimum accuracy of to be processed coordinates").setDefault(DEFAULT_COORDINATE_MIN_ACCURACY).type(Float.class);
        parser.addArgument("-e", "--epsilon")
                .help("Epsilon Parameter of RDP-Algorithm").setDefault(DEFAULT_RDP_EPSILON).type(Double.class);
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        String type = ns.getString("type");
        if (type.isEmpty()) throw new ArgumentParserException(parser);
        for (String name : ns.<String>getList("file")) {
            Path path = Paths.get(name);
            if (type.contains("p")) {
                new ProfileFileIOHandler(path);
            } else if (type.contains("r")) {
                Float minAccuracy = ns.getFloat("accuracy");
                Double rdpEpsilon = ns.getDouble("epsilon");
                new RideFileIOHandler(path, minAccuracy, rdpEpsilon);
            }
        }
    }
}
