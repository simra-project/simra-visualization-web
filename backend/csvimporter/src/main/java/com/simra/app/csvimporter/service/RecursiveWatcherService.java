package com.simra.app.csvimporter.service;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.simra.app.csvimporter.controller.*;

import com.simra.app.csvimporter.filter.MapMatchingService;
import com.simra.app.csvimporter.model.CSVFile;
import com.simra.app.csvimporter.model.ProfileEntity;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.nio.file.StandardWatchEventKinds.*;


@Service
public class RecursiveWatcherService implements MonitorService {

    private static final Logger LOG = LoggerFactory.getLogger(RecursiveWatcherService.class);

    @Value("${csv.monitor.path}")
    private File rootFolder;

    @Autowired
    private CSVFileRepository csvFileRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private MapMatchingService mapMatchingService;


    private WatchService watcher;

    private ExecutorService executor;


    @Value("${min_accuracy}")
    private float min_accuracy;

    @Value("${rdp_epsilion}")
    private double rdp_epsilion;

    @PostConstruct
    public void init() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        executor = Executors.newSingleThreadExecutor();
    }


    @PreDestroy
    public void cleanup() {
        try {
            watcher.close();
        } catch (IOException e) {
            LOG.error("Error closing watcher service", e);
        }
        executor.shutdown();
    }

    public void startRecursiveWatcher() {
        LOG.info("Starting Recursive Watcher");

        final Map<WatchKey, Path> keys = new HashMap<>();

        Consumer<Path> register = p -> {
            if (!p.toFile().exists() || !p.toFile().isDirectory()) {
                throw new RuntimeException("folder " + p + " does not exist or is not a directory");
            }
            try {
                Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        LOG.info("registering " + dir + " in watcher service");
                        WatchKey watchKey = dir.register(watcher, new WatchEvent.Kind[]{ENTRY_CREATE});
                        keys.put(watchKey, dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error registering path " + p);
            }
        };

        register.accept(rootFolder.toPath());

        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watcher.take(); // wait for a key to be available
                } catch (InterruptedException ex) {
                    return;
                }

                final Path dir = keys.get(key);
                if (dir == null) {
                    LOG.error("WatchKey " + key + " not recognized!");
                    continue;
                }

                key.pollEvents().stream()
                        .filter(e -> (e.kind() != OVERFLOW))
                        .map(e -> ((WatchEvent<Path>) e).context())
                        .forEach(p -> {
                            final Path absPath = dir.resolve(p);
                            if (absPath.toFile().isDirectory()) {
                                register.accept(absPath);
                            } else {
                                final File f = absPath.toFile();
                                LOG.info("Detected new file " + f.getAbsolutePath());
                                LOG.info("FileName " + f.getName());
                                if (f.getName().contains("VM")) {
                                    // Check of Already Parsed.
                                    if (csvFileRepository.findByFileId(f.getName()) == null) {
                                        // Differentiates Profile & Ride
                                        String type = "";
                                        try {
                                            String csvString = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                                            // Pass to correct Parser.
                                            if (csvString.contains("birth")) {
                                                // Handle as Profile
                                                type = "P";
                                                ProfileParser(f);
                                            } else {
                                                // Handle as incident and ride
                                                type = "R";
                                                // TODO Pass to Queue
                                                IncidentRideParser(f);

                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            LOG.error("File read failed: " + e.getMessage());

                                        }
                                        // Save status into Database.
                                        csvFileRepository.save(new CSVFile(f.getName(), type));
                                    } else {
                                        LOG.info("File already checked: " + f.getName());
                                    }
                                }
                            }
                        });
                boolean valid = key.reset(); // IMPORTANT: The key must be reset after processed
                if (!valid) {
                    break;
                }
            }
        });

    }

    private void ProfileParser(File f) {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(f))) {
            String line = reader.readLine();
            String[] arrOfStr = line.split("#");

            StringBuilder profileCSVwithHeader = new StringBuilder();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    profileCSVwithHeader.append(line);
                    profileCSVwithHeader.append("\r\n");
                }
            }

            ColumnPositionMappingStrategy<ProfileEntity> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(ProfileEntity.class);
            String[] memberFieldsToBindTo = {"birth", "gender", "region", "experience",
                    "numberOfRides", "duration", "numberOfIncidents", "waitedTime", "distance", "co2", "0", "1",
                    "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                    "21", "22", "23", "behaviour", "numberOfScary"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<ProfileEntity> profileBeans = new CsvToBeanBuilder<ProfileEntity>(new StringReader(profileCSVwithHeader.toString()))
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();
            if (!profileBeans.isEmpty()) {
                ProfileEntity profile = profileBeans.get(0);
                profile.setFileId(f.getName());
                profile.setAppVersion(arrOfStr[0]);
                profile.setFileVersion(Integer.parseInt(arrOfStr[1]));
                /*
                 * Any pre profile filter starts here.
                 */
                profileRepository.save(profile);
            }


        } catch (IOException e) {
            LOG.error(String.valueOf(e));
        }
    }


    private void IncidentRideParser(File f) {
        /*
         * Start thread to parse file and save incidents
         * TODO: QUEUE
         */
        IncidentParserThreaded incidentParserThreaded = new IncidentParserThreaded(f, incidentRepository);
        incidentParserThreaded.start();


        RideParserThreaded rideParserThreaded = new RideParserThreaded(f, rideRepository, min_accuracy, rdp_epsilion, mapMatchingService);
        rideParserThreaded.start();


    }
}