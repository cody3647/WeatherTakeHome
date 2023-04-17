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
    static Map<String, Path> filesMap;

    @BeforeAll
    static void setUp() throws IOException {
        testCsvFile = tempStorageDir.resolve("test.csv");

        String[] stations = {"aaa00000000", "aaa11111111", "aab00000000", "aab11111111","us1a0000111", "us1a0000222",
                             "us1b0000111", "us1b0000222"};
        LocalDate startDate = LocalDate.of(2023, 1, 1);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        HashSet<LocalDate> dates = new HashSet<>();
        for (int i = 0; i < 365; i++) {
            dates.add(startDate.plusDays(i));
        }
        String[] element = {"TAVG", "PRCP"};

        LinkedList<String> stationDataLines = new LinkedList<>();

        for (String station : stations) {
            for (LocalDate date : dates) {
                String element1 = station + "," + date.format(dateFormatter) + "," + element[0];
                String element2 = station + "," + date.format(dateFormatter) + "," + element[1];
                String remaining = date.getDayOfYear() + ",X,Y,Z,1300";
                stationDataLines.add(element1 + remaining);
                stationDataLines.add(element2 + remaining);
            }
        }

        Files.write(testCsvFile, stationDataLines, StandardCharsets.UTF_8, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    @AfterAll
    static void tearDown() throws IOException {
        new FileStationRecordLoader(tempStorageDir, Path.of("test.csv")).clearStorageDir();
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