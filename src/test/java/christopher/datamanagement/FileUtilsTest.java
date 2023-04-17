package christopher.datamanagement;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilsTest {
    @Test
    void getCsvStorageDir() {
        Path test = Path.of("test.csv");
        Path actual = FileUtils.getCsvStorageDir(Path.of("storage"), test);
        Path expected = FileStationRecordRetriever.BASE_STORAGE_DIR.resolve("test");
        assertEquals(expected, actual, "Does not return the correct storage path for given original csv file.");
    }

    @Test
    void removeExtensionString() {
        String testFilename = "test.file.csv";
        testFilename = FileUtils.removeExtension(testFilename);
        assertEquals("test.file", testFilename, "Extension was not remove from String properly: " + testFilename);
    }

    @Test
    void removeExtensionStringMissingExtension() {
        assertDoesNotThrow(() -> FileUtils.removeExtension("test"));

        String testFilename = "test";
        testFilename = FileUtils.removeExtension(testFilename);
        assertEquals("test", testFilename, "String should not have been changed: " + testFilename);
    }

    @Test
    void removeExtensionPath() {
        Path testPath = Path.of("test.file.csv");
        String testFilename = FileUtils.removeExtension(testPath);

        assertEquals("test.file", testFilename, "Extension was not remove from path properly: " + testFilename);
    }
}