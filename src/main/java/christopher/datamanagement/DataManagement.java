package christopher.datamanagement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class DataManagement {
    static public Path BASE_STORAGE_DIR = Path.of("storage");
    /**
     * Stations that start with these letters have more data entries and need to be split up more.
     * Map stores the number of characters to use for the split csv file names.
     */
    static Map<String, Integer> substringLengthsStationFiles = Map.of(
            "ASN", 8,
            "CA0", 6,
            "CA1", 6,
            "CHM", 7,
            "US1", 8,
            "USC", 8,
            "USR", 8,
            "USS", 8,
            "USW", 9
    );
    FileLoader fileLoader;
    FileRetriever fileRetriever;
    public DataManagement(Path filePath) throws IOException {
        fileLoader = loadFile(filePath);
        fileRetriever = new FileRetriever();

    }

    /**
     * Create and return the FileLoader with the given csv file
     * Logs the time taken to load the csv file.
     * @param csvFilePath Path of the master csv file
     * @return FileLoader
     * @throws IOException when an error occurs when reading the csv file
     */
    private FileLoader loadFile(Path csvFilePath) throws IOException {
        long start = System.currentTimeMillis();

        FileLoader fileLoader = new FileLoader(csvFilePath);

        long finish = System.currentTimeMillis();

        System.out.println("Time to load and split csv file: " + ((finish - start) / 1000.0 ));

        System.gc();
        return fileLoader;
    }

    /**
     * Helper funtion to get the name of the station file from a station ID
     * @param stationId String of the station ID
     * @return String filename where the station's data entries are located.
     */
    static public String getFileNameOfStation(String stationId) {
        String firstThree = stationId.substring(0,3).toUpperCase();
        if(substringLengthsStationFiles.containsKey(firstThree))
            return stationId.substring(0, substringLengthsStationFiles.get(firstThree)).toUpperCase() + ".csv";
        return firstThree + ".csv";
    }

    /**
     * Helper function to remove the extension from a file name and return the name as a String
     * @param filename String of filename with extension
     * @return String of filename without the extension
     */
    static public String removeExtension(String filename)  {
        int extStart = filename.lastIndexOf('.');
        return filename.substring(0, extStart);
    }

    /**
     * Helper function to remove the extension from a Path and return the name as a String
     * @param filePath Path of file
     * @return String of filename without extension
     */
    static public String removeExtension(Path filePath) {
        return removeExtension(filePath.getFileName().toString());
    }

}
