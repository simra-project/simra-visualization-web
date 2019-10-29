package main.java.com.simra.app.csvimporter.handler;

import java.nio.file.Path;

public class RideFileIOHandler extends FileIOHandler {


    public RideFileIOHandler(Path path) {
        super(path);
    }

    @Override
    public void fileParse() {
        // parses ride file, as its has different structure.
    }
}
