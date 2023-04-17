package christopher.web;

import christopher.datamanagement.FileStationRecordRetriever;
import christopher.datamanagement.FileTest;
import christopher.model.StationData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiTest {
    static HttpClient httpClient;
    @TempDir
    static Path tempStorageDir;
    static Path testCsvFile;
    static WebServer server;

    static ObjectMapper objectMapper = new ObjectMapper();


    @BeforeAll
    static void setup() throws Exception {
        httpClient = HttpClient.newBuilder().build();
        testCsvFile = FileTest.buildTestFile(tempStorageDir);
        FileStationRecordRetriever retriever = new FileStationRecordRetriever();
        retriever.loadCsvFile(testCsvFile);

        server = new WebServer(retriever);
        server.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        server.stop();
        FileTest.deleteTestFiles(tempStorageDir, testCsvFile);
    }

    @Test
    void apiStationIdFoundQuery() throws IOException, InterruptedException {
        String url = "http://localhost:8080/api?query=stations&id=";

        String stationId = "us1b0000222";
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url + stationId)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
        List<StationData> stationDataList =
                objectMapper.readValue(httpResponse.body(), new TypeReference<List<StationData>>() {
                });

        assertEquals(730, stationDataList.size());
        for (StationData stationData : stationDataList) {
            assertTrue(stationId.equalsIgnoreCase(stationData.getId()),
                       stationId + "(expected) is not equal to " + stationData.getId());
        }
    }

    @Test
    void apiStationIdFoundUrl() throws IOException, InterruptedException {
        String url = "http://localhost:8080/api/stations/";

        String stationId = "AAA00000000";
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url + stationId)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
        List<StationData> stationDataList =
                objectMapper.readValue(httpResponse.body(), new TypeReference<List<StationData>>() {
                });

        assertEquals(730, stationDataList.size());
        for (StationData stationData : stationDataList) {
            assertTrue(stationId.equalsIgnoreCase(stationData.getId()),
                       stationId + "(expected) is not equal to " + stationData.getId());
        }
    }

    @Test
    void apiStationsIdNotFoundQuery() throws IOException, InterruptedException {
        String url = "http://localhost:8080/api?query=stations&id=none";
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
        assertEquals("[]", httpResponse.body());
    }

    @Test
    void apiStationsIdNotFoundUrl() throws IOException, InterruptedException {
        String url = "http://localhost:8080/api/stations/none";
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
        assertEquals("[]", httpResponse.body());
    }

    @Test
    void uiPage() throws IOException, InterruptedException {
        String url = "http://localhost:8080";
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url)).build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());

    }
}
