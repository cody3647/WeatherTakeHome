package christopher.web;

import christopher.datamanagement.StationRecordRetriever;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class StationServlet extends HttpServlet {
    final static public String NAME = "station";
    StationRecordRetriever stationRecordRetriever;

    public StationServlet(StationRecordRetriever stationRecordRetriever) {
        this.stationRecordRetriever = stationRecordRetriever;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        String stationId = null;

        if(req.getDispatcherType() == DispatcherType.REQUEST) {
            String path = req.getPathInfo();
            if(path != null) {
                String[] pathPieces = path.split("/");

                if(pathPieces.length > 0)
                    stationId = pathPieces[1];
            }
        }
        else if(req.getDispatcherType() == DispatcherType.FORWARD) {
            stationId = req.getParameter("id");
        }

        if(stationId == null || stationId.isEmpty()) {
            out.print("[]");
            return;
        }

        List<String> list = stationRecordRetriever.getStationRecords(stationId);
        for(String line: list) {
            out.print(line);
            out.print("\n");
        }
    }
}
