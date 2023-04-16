package christopher.datamanagement;

import java.nio.file.Path;

public class FileUtils {
    /**
     * Helper function to get the storage directory to be used for storing the records of the given CSV file.
     * @param csvFilePath  Path of the CSV file
     * @return Path of the storage directory
     */
    static Path getCsvStorageDir(Path baseStorageDir, Path csvFilePath) {
        return baseStorageDir.resolve(
                removeExtension(csvFilePath));
    }

    /**
     * Helper function to remove the extension from a Path and return the name as a String
     *
     * @param filePath Path of file
     * @return String of filename without extension
     */
    static String removeExtension(Path filePath) {
        return removeExtension(filePath.getFileName().toString());
    }

    /**
     * Helper function to remove the extension from a file name and return the name as a String
     *
     * @param filename String of filename with extension
     * @return String of filename without the extension
     */
    static String removeExtension(String filename) {
        int extStart = filename.lastIndexOf('.');
        return filename.substring(0, extStart);
    }
}
