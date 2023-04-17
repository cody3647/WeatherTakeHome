package christopher;

import christopher.datamanagement.FileStationRecordRetriever;
import christopher.datamanagement.StationRecordRetriever;
import christopher.datamanagement.StoredStationRecordRetriever;
import christopher.web.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    static public final String USE_STORED = "--use-stored";
    private static final Logger LOGGER
            = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        if (areArgsInvalid(args)) {
            System.err.println("Please enter a valid csv file.  java <programName> <csv file>");
            return;
        }
        StationRecordRetriever stationRecordRetriever;

        try {
            Path csvFile = Path.of(args[0]);

            if(args.length > 1 && args[1].equals(USE_STORED))
                stationRecordRetriever = new StoredStationRecordRetriever();
            else
                stationRecordRetriever = new FileStationRecordRetriever();

            stationRecordRetriever.loadCsvFile(csvFile);

        }
        catch (IOException e) {
            LOGGER.error("Error loading csv file", e);
            return;
        }

        try {
            WebServer server = new WebServer(stationRecordRetriever);
            server.start();
        }
        catch (Exception e) {
            LOGGER.error("The server encountered an exception", e);
        }
    }



    /**
     * Checks that the args passed to main are valid or not.
     * Checks that there is a first argument and that it is a readable regular file.
     *
     * @param args String[] of args passed to main
     * @return boolean returns true if the arguments are invalid.
     */
    static public boolean areArgsInvalid(String[] args) {
        if (args.length < 1)
            return true;

        Path filePath = Path.of(args[0]);

        return !(Files.isRegularFile(filePath) && Files.isReadable(filePath));
    }

}
