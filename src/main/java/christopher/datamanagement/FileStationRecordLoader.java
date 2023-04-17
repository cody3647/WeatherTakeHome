package christopher.datamanagement;

import christopher.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

class FileStationRecordLoader {

    /**
     * Map of filenames without extensions to the Path of the file
     */
    private final HashMap<String, Path> filesMap;
    private final Path csvFilePath;
    private final Path storageDir;

    /**
     * Loads the csv file into the storage directory as multiple sub-files sorted by station ID.
     * <p>
     * A subdirectory under baseStorageDir is created with the name of the csv file
     * to store the sub-files/directories in.
     *
     * @param baseStorageDir Path to create storage directory under
     * @param csvFilePath Path of the csv file to load
     */
    public FileStationRecordLoader(Path baseStorageDir, Path csvFilePath) {
        filesMap = new HashMap<>();
        this.csvFilePath = csvFilePath;
        this.storageDir = FileUtils.getCsvStorageDir(baseStorageDir, csvFilePath);
    }

    /**
     * Clear the storage directory that will be used for this file.  Then load the file and split it into smaller files
     * in the storage directory
     * @throws IOException when there is an error reading the csv file or writing the sub-files.
     */
    void load() throws IOException {
        clearStorageDir();
        Main.printUsedMemory();
        loadFile();
        System.gc();
    }

    /**
     * Deletes the csv file storage directory and files and subdirectories in the storage directory.
     *
     * @throws IOException if an error occurs while walking and deleting the files.
     */
    void clearStorageDir() throws IOException {
        if(Files.exists(storageDir))
            Files.walkFileTree(storageDir, new FileDirectoryDeleterVisitor());
    }

    /**
     * Loads all the lines in the csvFile into a map keyed by station ID filename.
     *
     * @throws IOException if an error occurs while reading the file.
     */
    void loadFile() throws IOException {
        ConcurrentMap<String, List<String>> tempStorageMap;

        /*
        Read 2,000,000 million lines in at a time to keep memory usage low
        Then take a parallel stream from the list and group them in a concurrent map with the station id filename as
        the keys and a list of csv string records as the values.
         */
        try (BufferedReader bufferedReader = Files.newBufferedReader(csvFilePath)) {
            String line;
            int count = 0;
            LinkedList<String> lines = new LinkedList<>();

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
                count++;

                if (count == 2_000_000) {
                    tempStorageMap = processLines(lines);
                    lines = new LinkedList<>();
                    writeSplitCsvFiles(storageDir, tempStorageMap);
                    Main.printUsedMemory();
                    System.gc();
                    count = 0;
                }
            }

            // Anything left in the last lines
            tempStorageMap = processLines(lines);
            lines = null;
            Main.printUsedMemory();
            System.gc();
            writeSplitCsvFiles(storageDir, tempStorageMap);
        }
    }

    ConcurrentMap<String, List<String>> processLines(LinkedList<String> lines) {
        return lines.parallelStream().collect(
                Collectors.groupingByConcurrent(FileStationRecordRetriever::getFileNameOfStation,
                                                Collectors.toList()));
    }

    /**
     * Writes each entry in the map into a sub-file, under a subdirectory of the first character of the filename.
     * Filenames are the keys of the map and each entry in the list is written on a new line.
     *
     * @param storageDir Path of the storage directory to write files to.
     * @param storageMap ConcurrentMap of filenames to lists of lines to write.
     */
    void writeSplitCsvFiles(final Path storageDir, ConcurrentMap<String, List<String>> storageMap) {
        // Parallelize the stream
        storageMap.entrySet().stream().parallel().forEach(listEntry -> {
            String filename = listEntry.getKey();
            List<String> lines = listEntry.getValue();

            try {
                // create subdirectory path storage/original-csv-file/firstChar/sub-csv-file.csv
                Path storageSubDir = storageDir.resolve(Path.of(filename.substring(0, 1)));
                Files.createDirectories(storageSubDir);
                Path storageFile = storageSubDir.resolve(filename);
                filesMap.put(FileUtils.removeExtension(filename), storageFile);

                // Write the lines to the sub-file
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(storageFile, StandardOpenOption.CREATE,
                                                                             StandardOpenOption.WRITE,
                                                                             StandardOpenOption.APPEND))
                {
                    for (String line : lines) {
                        bufferedWriter.write(line);
                        bufferedWriter.write("\n");
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public Map<String, Path> getFilesMap() {
        return Collections.unmodifiableMap(filesMap);
    }

    /**
     * A SimpleFileVisitor for deleting all files and subdirectories.
     */
    static private class FileDirectoryDeleterVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null)
                throw exc;

            Files.delete(dir);

            return FileVisitResult.CONTINUE;
        }
    }
}
