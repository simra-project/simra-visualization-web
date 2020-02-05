package com.simra.app.csvimporter.service;


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

    public void startRecursiveWatcher() throws Exception {
        LOG.info("Starting Recursive Watcher from {}", this.rootFolder);
        FileAlterationObserver observer = new FileAlterationObserver(this.rootFolder);
        observer.addListener(simraFileAlterationListenerAdaptor);
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000, observer);
        monitor.start();
    }

}