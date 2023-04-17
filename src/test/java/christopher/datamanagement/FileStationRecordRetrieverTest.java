package christopher.datamanagement;

import christopher.model.StationData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileStationRecordRetrieverTest {

    @TempDir
    static Path tempStorageDir;
    static Path testCsvFile;


    @BeforeAll
    static void setUp() throws IOException {
        testCsvFile = FileTest.buildTestFile(tempStorageDir);

    }

    @AfterAll
    static void tearDown() throws IOException {
        FileTest.deleteTestFiles(tempStorageDir, testCsvFile);
    }

    @Test
    void getStationDataList() throws IOException {
        FileStationRecordRetriever retriever = new FileStationRecordRetriever();
        retriever.loadCsvFile(testCsvFile);

        List<StationData> stationDataList;
        String stationId;

        stationId = "aaa00000000";
        stationDataList = retriever.getStationDataList(stationId);
        for(StationData stationData: stationDataList)
            assertEquals(stationId, stationData.getId(), "Wrong station data returned");

        stationId = "us1b0000222";
        stationDataList = retriever.getStationDataList(stationId);
        for(StationData stationData: stationDataList)
            assertEquals(stationId, stationData.getId(), "Wrong station data returned");

        stationId = "none";
        String finalStationId = stationId;
        assertDoesNotThrow(() -> retriever.getStationDataList(finalStationId));
        stationDataList = retriever.getStationDataList(stationId);
        assertEquals(0, stationDataList.size(), "List should be empty");
    }

    @Test
    void getRawStationDataList() throws IOException {
        FileStationRecordRetriever retriever = new FileStationRecordRetriever();
        retriever.loadCsvFile(testCsvFile);

        List<String> lineList;
        String stationId;

        stationId = "aaa00000000";
        lineList = retriever.getRawStationDataList(stationId);
        for(String line: lineList)
            assertTrue(line.startsWith(stationId), "Wrong station data returned");

        stationId = "us1b0000222";
        lineList = retriever.getRawStationDataList(stationId);
        for(String line: lineList)
            assertTrue(line.startsWith(stationId), "Wrong station data returned");

        stationId = "none";
        String finalStationId = stationId;
        assertDoesNotThrow(() -> retriever.getRawStationDataList(finalStationId));
        lineList = retriever.getRawStationDataList(stationId);
        assertEquals(0, lineList.size(), "List should be empty");
    }

    @Test
    void getFileNameOfStation() {
        Map.of("ASN", 8, "CA0", 6, "CA1", 6, "CHM", 7, "US1", 8,
               "USC", 8, "USR", 8, "USS", 8, "USW", 9);

        String actual;
        String stationId;
        String msg = "Incorrect filename returns for station id: ";

        stationId = "abcdefghijk";
        actual = FileStationRecordRetriever.getFileNameOfStation(stationId);
        assertEquals("ABC.csv", actual, msg + stationId);

        stationId = "abcdefghijk".toUpperCase();
        actual = FileStationRecordRetriever.getFileNameOfStation(stationId);
        assertEquals("ABC.csv", actual, msg + stationId);

        stationId = "ASN45678911";
        actual = FileStationRecordRetriever.getFileNameOfStation(stationId);
        assertEquals("ASN45678.csv", actual, msg + stationId);

        stationId = "CA045678911";
        actual = FileStationRecordRetriever.getFileNameOfStation(stationId);
        assertEquals("CA0456.csv", actual, msg + stationId);

    }
}