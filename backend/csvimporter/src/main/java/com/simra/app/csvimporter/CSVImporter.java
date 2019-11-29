package main.java.com.simra.app.csvimporter;

import main.java.com.simra.app.csvimporter.handler.ProfileDirectoryIOHandler;
import main.java.com.simra.app.csvimporter.handler.RideFileIOHandler;
import main.java.com.simra.app.csvimporter.services.ConfigService;
import main.java.com.simra.app.csvimporter.services.ThreadController;
import main.java.com.simra.app.csvimporter.services.StatisticsService;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CSVImporter {
    private static final Logger logger = Logger.getLogger(CSVImporter.class);

    private static ArgumentParser parser;


    public static void main(String[] args) {

        ConfigService config = new ConfigService();
        config.readProperties();

        logger.info("CSV Import Application Started");
        try {
            Namespace ns = null;
            parser = ArgumentParsers.newFor("CSVImporter").build()
                    .defaultHelp(true)
                    .description("Import CSV file.");
            parser.addArgument("-t", "--type")
                    .choices("r", "p").setDefault("r")
                    .help("Specify file type r <- ride, p <- profile");
            parser.addArgument("-p", "--path")
                    .choices("f", "d").setDefault("f")
                    .help("Specify path type f <- single file , d <- directory");
            parser.addArgument("file").nargs("?")
                    .help("File to read");
            parser.addArgument("-a", "--accuracy")
                    .help("Minimum accuracy of to be processed coordinates").setDefault(Float.valueOf(ConfigService.config.getProperty("min_accuracy")));
            parser.addArgument("-e", "--epsilon")
                    .help("Epsilon Parameter of RDP-Algorithm").setDefault(Double.valueOf(ConfigService.config.getProperty("rdp_epsilon")));
            parser.addArgument("--statistics")
                    .help("Use `--statistics true` to only recalculate the statistics");

            ns = parser.parseArgs(args);

            if (ns.getString("statistics").equals("true")) {
                (new StatisticsService()).calculateStatistics();
                return;
            }

            String type = ns.getString("type");
            if (type.isEmpty()) {
                throw new ArgumentParserException(parser);
            }
            /*Start UI For Folder Import*/
            String pathType = ns.getString("path");
            if (pathType.contains("d")) {
                String folder = ns.getString("file");

                try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
                    ArrayList<Path> pathsToImport = (ArrayList<Path>) walk.filter(Files::isRegularFile).map(Path::toAbsolutePath).collect(Collectors.toList());

                    Float minAccuracy = Float.valueOf(ns.getString("accuracy"));
                    Double rdpEpsilon = Double.valueOf(ns.getString("epsilon"));

                    ThreadController threadController = new ThreadController(pathsToImport, type, minAccuracy, rdpEpsilon);
                    threadController.executeFileRead();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String name = ns.getString("file");
                Path path = Paths.get(name);
                if (type.contains("p")) {
                    new ProfileDirectoryIOHandler(path);
                } else if (type.contains("r")) {
                    Float minAccuracy = Float.valueOf(ns.getString("accuracy"));
                    Double rdpEpsilon = Double.valueOf(ns.getString("epsilon"));
                    RideFileIOHandler rideFileIOHandler = new RideFileIOHandler(path, minAccuracy, rdpEpsilon);
                    rideFileIOHandler.parseFile();
                }
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            logger.error(e);
            System.exit(1);
        }
    }
}
