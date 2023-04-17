package christopher.datamanagement;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

class FileStationRecordLoaderTest{
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

    @DisplayName("Clear Storage Directory: deletes all files and subdirectories")
    @Test
    void clearStorageDirExists() throws IOException {
        // Create subdirectories and files
        Path clearStorageTestPath = tempStorageDir.resolve("clearTest");
        Files.createDirectories(clearStorageTestPath);
        Path file1 = Path.of("test1.csv");
        Path file2 = Path.of("test2.csv");
        for(char c = 'A'; c <= 'D'; c++) {
            Path subDirectory = clearStorageTestPath.resolve(String.valueOf(c));
            Files.createDirectory(subDirectory);
            Files.createFile(subDirectory.resolve(file1));
            Files.createFile(subDirectory.resolve(file2));
        }

        // Check that they were created
        for(char c = 'A'; c <= 'D'; c++) {
            Path subDirectory = clearStorageTestPath.resolve(String.valueOf(c));
            assertTrue(Files.exists(subDirectory), "Subdirectory was not created for test.");
            assertTrue(Files.exists(subDirectory.resolve(file1)), "File was not created for test.");
            assertTrue(Files.exists(subDirectory.resolve(file2)), "File was not created for test.");
        }

        // Delete them
        FileStationRecordLoader loader = new FileStationRecordLoader(tempStorageDir, Path.of("clearTest.csv"));
        loader.clearStorageDir();

        // Check that they were deleted
        for(char c = 'A'; c <= 'D'; c++) {
            Path subDirectory = clearStorageTestPath.resolve(String.valueOf(c));
            assertFalse(Files.exists(subDirectory), "Subdirectory was not deleted: " + subDirectory);
            assertFalse(Files.exists(subDirectory.resolve(file1)), "File was not deleted: " + file1);
            assertFalse(Files.exists(subDirectory.resolve(file2)), "File was not deleted." + file2);
        }
    }

    @DisplayName("ClearStorageDir: directory does not exist")
    @Test
    void clearStorageDirNotExists() throws IOException {
        Path clearStorageTestPath = tempStorageDir.resolve("clearTest");
        FileStationRecordLoader loader = new FileStationRecordLoader(tempStorageDir, Path.of("clearTest.csv"));
        assertDoesNotThrow(loader::clearStorageDir, "Throws an exception when directory " +
                                                    "does not exist.");
    }


    @DisplayName("LoadFileToMap: Correct number of partitions and lines found")
    @Test
    void loadFileToMap() throws IOException {
        FileStationRecordLoader loader = new FileStationRecordLoader(tempStorageDir, testCsvFile);
        ConcurrentMap<String, List<String>> stationMap = loader.loadFileToMap();

        assertEquals(4, stationMap.size(), "Incorrect number of stations found.");
        for(Map.Entry<String, List<String>> entry: stationMap.entrySet()) {
            List<String> list = entry.getValue();
            String fileNoExt = FileUtils.removeExtension(entry.getKey());

            csvLinesTest(list, fileNoExt);
        }
    }

    @DisplayName("WriteSplitCsvFiles: Correct number of files/subdirectories created and lines written")
    @Test
    void writeSplitCsvFiles() throws IOException {
        Path fileStorageDir = tempStorageDir.resolve("test");

        FileStationRecordLoader loader = new FileStationRecordLoader(tempStorageDir, testCsvFile);
        ConcurrentMap<String, List<String>> stationMap = loader.loadFileToMap();

        loader.writeSplitCsvFiles(fileStorageDir, stationMap);

        FileCounter fileCounter = new FileCounter();
        Files.walkFileTree(fileStorageDir, fileCounter);

        assertEquals(4, fileCounter.files.size(), "Incorrect number of files created: \n" + fileCounter.files);
        assertEquals(3, fileCounter.dirs.size(), "Incorrect number of subdirectories created: \n" + fileCounter.dirs);

        Path aDir = fileStorageDir.resolve("A");
        Path uDir = fileStorageDir.resolve("U");
        Path aaaFile = aDir.resolve("AAA.csv");
        Path aabFile = aDir.resolve("AAB.csv");
        Path us1aFile = uDir.resolve("US1A0000.csv");
        Path us1bFile = uDir.resolve("US1B0000.csv");

        String msg = " does not exist";
        assertTrue(Files.exists(aaaFile), aaaFile + msg);
        assertTrue(Files.exists(aaaFile), aabFile + msg);
        assertTrue(Files.exists(us1aFile), us1aFile + msg);
        assertTrue(Files.exists(us1bFile), us1bFile + msg);

        msg = " is not readable";
        assertTrue(Files.isReadable(aaaFile), aaaFile + msg);
        assertTrue(Files.isReadable(aaaFile), aabFile + msg);
        assertTrue(Files.isReadable(us1aFile), us1aFile + msg);
        assertTrue(Files.isReadable(us1bFile), us1bFile + msg);

        msg = " is not a regular file";
        assertTrue(Files.isRegularFile(aaaFile), aaaFile + msg);
        assertTrue(Files.isRegularFile(aaaFile), aabFile + msg);
        assertTrue(Files.isRegularFile(us1aFile), us1aFile + msg);
        assertTrue(Files.isRegularFile(us1bFile), us1bFile + msg);

        String fileNoExt;

        fileNoExt = FileUtils.removeExtension(aaaFile);
        csvLinesTest(Files.readAllLines(aaaFile), fileNoExt);

        fileNoExt = FileUtils.removeExtension(aabFile);
        csvLinesTest(Files.readAllLines(aabFile), fileNoExt);

        fileNoExt = FileUtils.removeExtension(us1aFile);
        csvLinesTest(Files.readAllLines(us1aFile), fileNoExt);

        fileNoExt = FileUtils.removeExtension(us1bFile);
        csvLinesTest(Files.readAllLines(us1bFile), fileNoExt);
    }

    void csvLinesTest(List<String> list, String fileNoExt) {
        assertEquals(1460, list.size(), "Incorrect number of lines for" + fileNoExt);
        Collections.sort(list);
        String first = list.get(0).toUpperCase();
        String last = list.get(list.size() - 1).toUpperCase();

        assertTrue(first.toUpperCase().startsWith(fileNoExt),
                   "First line in wrong list: " + fileNoExt + " != " + first);
        assertTrue(last.toUpperCase().startsWith(fileNoExt),
                   "Last line in wrong list: " + fileNoExt + " != " + last);

        assertFalse(first.substring(0,12).equalsIgnoreCase(last.substring(0,12)),
                    "First and last lines should have different station IDs");
    }

    static class FileCounter extends SimpleFileVisitor<Path> {
        List<Path> files = new ArrayList<>();
        List<Path> dirs = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            files.add(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null)
                throw exc;

            dirs.add(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}