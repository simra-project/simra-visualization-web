package main.java.com.simra.app.csvimporter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVImporter {

    private static final Logger logger = Logger.getLogger(CSVImporter.class);


    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("CSVImporter").build()
                .defaultHelp(true)
                .description("Import CSV file.");
        parser.addArgument("-t", "--type")
                .choices("r", "p").setDefault("r")
                .help("Specify file type r <- ride, p <- profile");
        parser.addArgument("file").nargs("*")
                .help("File to read");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        String type= ns.getString("type");
        if(type.isEmpty()) throw new ArgumentParserException(parser);
        for (String name : ns.<String> getList("file")) {
            Path path = Paths.get(name);
            logger.info(path.getFileName());
        }

    }

}
