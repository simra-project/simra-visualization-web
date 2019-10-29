package main.java.com.simra.app.csvimporter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CSVImporter {

    private static final Logger logger = Logger.getLogger(CSVImporter.class);


    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("CSVImporter").build()
                .defaultHelp(true)
                .description("Import CSV file.");
        parser.addArgument("-t", "--type")
                .choices("r", "p").setDefault("r")
                .help("Specify file type r <- ride, p <- profile");
        parser.addArgument("file").nargs("*")
                .help("File to calculate checksum");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(ns.getString("type"));
        } catch (NoSuchAlgorithmException e) {
            logger.error(String.format("Could not get instance of algorithm %s: %s",
                    ns.getString("type"), e.getMessage()));
            System.exit(1);
        }
        for (String name : ns.<String> getList("file")) {
            Path path = Paths.get(name);
            try (ByteChannel channel = Files.newByteChannel(path,
                    StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while (channel.read(buffer) > 0) {
                    buffer.flip();
                    digest.update(buffer);
                    buffer.clear();
                }
            } catch (IOException e) {
                logger.error("failed to read data: ", e);
            }

        }

    }

}
