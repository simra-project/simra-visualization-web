package com.simra.app.csvimporter.controller;


import java.io.IOException;

public interface MonitorService {
    public void init() throws IOException;

    public void startRecursiveWatcher();

}
