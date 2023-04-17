package christopher.datamanagement;

import christopher.model.StationData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileStationRecordRetriever implements StationRecordRetriever {
    static public Path BASE_STORAGE_DIR = Path.of("storage");
    /**
     * Stations that start with these letters have more data entries and need to be split up more.
     * Map stores the number of characters to use for the split csv file names.
     */
    static final Map<String, Integer> substringLengthsStationFiles =
            Map.of("ASN", 8, "CA0", 6, "CA1", 6, "CHM", 7, "US1", 8,
                   "USC", 8, "USR", 8, "USS", 8, "USW", 9);

    /**
     * Filename without extension => file path
     */
    Map<String, Path> filesMap;

    public FileStationRecordRetriever() {
    }

    /**
     * @param stationId String of the station ID whose records we want
     * @return List of StationData that match the station ID
     * @throws IOException
     */
    @Override
    public List<StationData> getStationDataList(String stationId) throws IOException {
        String filename = FileStationRecordRetriever.getFileNameOfStation(stationId);
        filename = FileUtils.removeExtension(filename);

        Path filePath = filesMap.get(filename);

        if(filePath == null)
            return List.of();

        LineFilter lineFilter = new LineFilter(stationId);

        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.filter(lineFilter).map(StationData::createStationDataFromCsvRecord)
                        .filter(Objects::nonNull).toList();
        }
    }

    /**
     * @param stationId String of the station ID whose records we want
     * @return List of String records that match the station ID
     * @throws IOException
     */
    @Override
    public List<String> getRawStationDataList(String stationId) throws IOException {
        String filename = FileStationRecordRetriever.getFileNameOfStation(stationId);
        filename = FileUtils.removeExtension(filename);

        Path filePath = filesMap.get(filename);

        if(filePath == null)
            return List.of();

        LineFilter lineFilter = new LineFilter(stationId);

        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.filter(lineFilter).toList();
        }
    }

    /**
     * Create the FileStationRecordLoader with the given csv file
     * Logs the time taken to load the csv file.
     *
     * @param csvFilePath Path of the master csv file
     * @throws IOException when an error occurs when reading the csv file
     */
    @Override
    public void loadCsvFile(Path csvFilePath) throws IOException {
        long start = System.currentTimeMillis();

        FileStationRecordLoader stationRecordLoader = new FileStationRecordLoader(BASE_STORAGE_DIR, csvFilePath);
        stationRecordLoader.load();
        filesMap = stationRecordLoader.getFilesMap();

        long finish = System.currentTimeMillis();

        System.out.println("Time to load and split csv file: " + ((finish - start) / 1000.0));

        System.gc();
    }

    /**
     * Helper funtion to get the name of the station file from a station ID
     *
     * @param stationId String of the station ID
     * @return String filename where the station's data entries are located.
     */
    static String getFileNameOfStation(String stationId) {
        String firstThree = stationId.substring(0, 3).toUpperCase();
        if (substringLengthsStationFiles.containsKey(firstThree))
            return stationId.substring(0, substringLengthsStationFiles.get(firstThree)).toUpperCase() + ".csv";
        return firstThree + ".csv";
    }

    /**
     * Predicate for filtering lines that match the station id.
     */
    static class LineFilter implements Predicate<String> {
        final String stationId;

        public LineFilter(String stationId) {
            this.stationId = stationId;
        }

        @Override
        public boolean test(String line) {
            // Station IDs must be 11 characters long, region matches returns false if toffset (0) + len (11)
            // is greater than the length of stationId
            return stationId.regionMatches(true, 0, line, 0, 11);
        }
    }

}
