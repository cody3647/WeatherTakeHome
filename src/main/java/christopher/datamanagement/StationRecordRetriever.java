package christopher.datamanagement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A StationRecordRetriever must be able to load a csv file and retrieve records by station id.
 */
public interface StationRecordRetriever {
    /**
     * Returns a list of records from the given station
     *
     * @param stationId String of the station ID whose records we want
     * @return List of the string records from the station
     * @throws IOException when an error is encountered when retrieving the records
     */
    List<String> getStationRecords(String stationId) throws IOException;

    /**
     * Loads the given csv file.
     *
     * @param csvFilePath Path of the csv file to load
     */
    void loadCsvFile(Path csvFilePath) throws IOException;
}
