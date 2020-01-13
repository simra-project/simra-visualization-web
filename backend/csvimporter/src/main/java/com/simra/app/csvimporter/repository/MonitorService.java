package com.simra.app.csvimporter.repository;


import java.io.IOException;

public interface MonitorService {
    public void init() throws IOException;

    public void startRecursiveWatcher();

}
