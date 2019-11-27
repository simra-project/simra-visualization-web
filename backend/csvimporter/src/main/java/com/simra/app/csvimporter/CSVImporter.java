package main.java.com.simra.app.csvimporter;

import main.java.com.simra.app.csvimporter.handler.ProfileDirectoryIOHandler;
import main.java.com.simra.app.csvimporter.handler.RideDirectoryIOHandler;
import main.java.com.simra.app.csvimporter.services.ConfigService;
import main.java.com.simra.app.csvimporter.services.ThreadController;
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

    private static Float DEFAULT_COORDINATE_MIN_ACCURACY = 20f;
    private static Double DEFAULT_RDP_EPSILON = 0.0000001;

    private static  ArgumentParser parser;

    public static void main(String[] args) {

        ConfigService.readProperties();

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
                    .help("Minimum accuracy of to be processed coordinates").setDefault(Float.parseFloat(ConfigService.config.getProperty("min_accuracy")));
            parser.addArgument("-e", "--epsilon")
                    .help("Epsilon Parameter of RDP-Algorithm").setDefault(Double.parseDouble(ConfigService.config.getProperty("rdp_epsilon")));

            ns = parser.parseArgs(args);
            String type = ns.getString("type");
            if (type.isEmpty()) {
                throw new ArgumentParserException(parser);
            }
            /*Start UI For Folder Import*/
            String pathType = ns.getString("path");
            if(pathType.contains("d")){
                String folder = ns.getString("file");

                try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
                    ArrayList<String> results = (ArrayList<String>) walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());

                    Float minAccuracy = ns.getFloat("accuracy");
                    Double rdpEpsilon = ns.getDouble("epsilon");

                    ThreadController threadController= new ThreadController(results, type, minAccuracy, rdpEpsilon);
                    threadController.executeFileRead();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                String name = ns.getString("file");
                Path path = Paths.get(name);
                if (type.contains("p")) {
                    new ProfileDirectoryIOHandler(path);
                } else if (type.contains("r")) {
                    Float minAccuracy = ns.getFloat("accuracy");
                    Double rdpEpsilon = ns.getDouble("epsilon");
                    new RideDirectoryIOHandler(path, minAccuracy, rdpEpsilon);
                }
            }
        } catch ( ArgumentParserException e) {
            parser.handleError(e);
            logger.error(e);
            System.exit(1);
        }

    }
}
