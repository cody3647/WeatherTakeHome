package christopher.datamanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class StoredStationRecordRetriever extends FileStationRecordRetriever{
    private static final Logger LOGGER
            = LoggerFactory.getLogger(StoredStationRecordRetriever.class);

    public StoredStationRecordRetriever() {
        super();
        super.filesMap = new HashMap<>();
    }

    /**
     * Used to find the associated stored split files of the given csv file.
     * Does not parse the csv file and therefore no guarantee is made that the split
     * csv files are the same as the given csv file.
     * @param csvFilePath Path of the csv file to load
     * @throws IOException if there is an error finding the files in the storage directory
     */
    @Override
    public void loadCsvFile(Path csvFilePath) throws IOException {
        Path storageDir = FileUtils.getCsvStorageDir(BASE_STORAGE_DIR, csvFilePath);

        Files.walkFileTree(storageDir, new FileDirectoryFinderVisitor(filesMap));
        LOGGER.info("Found {} sub-files from previous loading of {}", filesMap.size(), csvFilePath);
    }

    /**
     * A SimpleFileVisitor for deleting all files and subdirectories.
     */
    static private class FileDirectoryFinderVisitor extends SimpleFileVisitor<Path> {
        final Map<String, Path> filesMap;
        public FileDirectoryFinderVisitor(Map<String, Path> filesMap) {
            this.filesMap = filesMap;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            filesMap.put(FileUtils.removeExtension(file), file);

            return FileVisitResult.CONTINUE;
        }
    }
}
