package christopher.datamanagement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;

public class FileTest {
    public static Path buildTestFile(Path tempStorageDir) throws IOException {
        Path testCsvFile;
        FileStationRecordRetriever.BASE_STORAGE_DIR = tempStorageDir;

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
                String element1 = station + "," + date.format(dateFormatter) + "," + element[0] + ",";
                String element2 = station + "," + date.format(dateFormatter) + "," + element[1] + ",";
                String remaining = date.getDayOfYear() + ",X,Y,Z,1300";
                stationDataLines.add(element1 + remaining);
                stationDataLines.add(element2 + remaining);
            }
        }

        Files.write(testCsvFile, stationDataLines, StandardCharsets.UTF_8, StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return testCsvFile;
    }

    public static void deleteTestFiles(Path tempStorageDir, Path testCsvFile) throws IOException {
        FileStationRecordRetriever.BASE_STORAGE_DIR = Path.of("storage");
        Files.delete(testCsvFile);
        new FileStationRecordLoader(tempStorageDir, testCsvFile).clearStorageDir();
    }
}
