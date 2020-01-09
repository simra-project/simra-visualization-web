package com.simra.app.csvimporter.service;


import com.simra.app.csvimporter.controller.*;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;


@Service
public class RecursiveWatcherService implements MonitorService {

    private static final Logger LOG = LoggerFactory.getLogger(RecursiveWatcherService.class);

    @Value("${csv.monitor.path}")
    private File rootFolder;

    @Autowired
    private SimraFileAlterationListenerAdaptor simraFileAlterationListenerAdaptor;

    private void addFileAlterationObserver(FileAlterationMonitor monitor, FileAlterationListener listener,
                                           File rootDir) {
        // Add a observer for the current root directory
        FileAlterationObserver observer = new FileAlterationObserver(rootDir);
        observer.addListener(listener);
        monitor.addObserver(observer);

        // List subdirectories under the current root directory
        File[] subDirs = rootDir.listFiles(File::isDirectory);

        if (subDirs == null || subDirs.length == 0) {
            return;
        }

        // Recursively add a observer for each subdirectory
        for (File subDir : subDirs) {
            LOG.info("Adding listener to folder: {}", subDir.getPath());
            this.addFileAlterationObserver(monitor, listener, subDir);
        }
    }

    public void startRecursiveWatcher() throws Exception {
        LOG.info("Starting Recursive Watcher");

        FileAlterationObserver observer = new FileAlterationObserver(this.rootFolder);
        observer.addListener(simraFileAlterationListenerAdaptor);
        FileAlterationMonitor monitor = new FileAlterationMonitor(500, observer);
        this.addFileAlterationObserver(monitor, simraFileAlterationListenerAdaptor, this.rootFolder);
        monitor.start();
    }

}