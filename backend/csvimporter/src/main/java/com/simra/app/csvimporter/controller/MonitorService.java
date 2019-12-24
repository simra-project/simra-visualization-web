package com.simra.app.csvimporter.controller;


import java.io.IOException;

public interface MonitorService {
    void init() throws IOException;

    void startRecursiveWatcher();

}
