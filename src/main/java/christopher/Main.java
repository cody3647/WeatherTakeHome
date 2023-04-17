package christopher;

import christopher.datamanagement.FileStationRecordRetriever;
import christopher.datamanagement.StationRecordRetriever;
import christopher.datamanagement.StoredStationRecordRetriever;
import christopher.web.WebServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    static public final String USE_STORED = "--use-stored";
    public static void main(String[] args) {
        if (areArgsInvalid(args)) {
            System.err.println("Please enter a valid csv file.  java program <csv file>");
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
            System.err.println("Error loading csv file: " + e.getMessage());
            return;
        }

        try {
            WebServer server = new WebServer(stationRecordRetriever);
            server.start();
        }
        catch (Exception e) {
            System.err.println("The server encountered an exception");
            e.printStackTrace();
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


    static public void printUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Memory: " + (runtime.totalMemory() - runtime.freeMemory()));
    }

}
