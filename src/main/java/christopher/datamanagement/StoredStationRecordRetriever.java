package christopher.datamanagement;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class StoredStationRecordRetriever extends FileStationRecordRetriever{

    public StoredStationRecordRetriever() {
        super();
        super.filesMap = new HashMap<>();
    }

    /**
     * Used to find the associated stored split files of the given csv file.
     * Does not parse the csv file and therefore no guarantee is made that the split
     * csv files are the same as the given csv file.
     * @param csvFilePath Path of the csv file to load
     * @throws IOException
     */
    @Override
    public void loadCsvFile(Path csvFilePath) throws IOException {
        Path storageDir = getCsvStorageDir(csvFilePath);

        Files.walkFileTree(storageDir, new FileDirectoryFinderVisitor(filesMap));
    }

    /**
     * A SimpleFileVisitor for deleting all files and subdirectories.
     */
    static private class FileDirectoryFinderVisitor extends SimpleFileVisitor<Path> {
        Map<String, Path> filesMap;
        public FileDirectoryFinderVisitor(Map<String, Path> filesMap) {
            this.filesMap = filesMap;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            filesMap.put(removeExtension(file), file);

            return FileVisitResult.CONTINUE;
        }
    }
}