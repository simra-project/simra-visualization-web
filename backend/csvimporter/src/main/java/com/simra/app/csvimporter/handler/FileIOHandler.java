package main.java.com.simra.app.csvimporter.handler;

import java.io.File;
import java.nio.file.Path;

public abstract class FileIOHandler {

    private Path path;

    FileIOHandler(Path path) {
        this.path=path;
    }

    public boolean canOpenFile(){
        File file= new File(String.valueOf(this.path.toFile()));
        return file.canRead();
    }

    Path getPath() {
        return path;
    }

    public void setPath(Path path){
        this.path=path;
    }

    public abstract void fileParse();
}
