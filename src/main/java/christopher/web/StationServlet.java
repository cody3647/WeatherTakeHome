package christopher.web;

import christopher.datamanagement.StationRecordRetriever;
import christopher.model.QueryResults;
import christopher.model.StationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet to handle the stations api queries.
 */
public class StationServlet extends HttpServlet {
    final static public String NAME = "stations";
    final static public String PARAM_ID = "id";
    final static public String PARAM_FORMAT = "format";
    final static public String FORMAT_CSV = "csv";
    final static public String FORMAT_CSV_TYPE = "text/plain";
    final static public String FORMAT_JSON = "json";
    final static public String FORMAT_JSON_TYPE = "application/json";
    StationRecordRetriever stationRecordRetriever;
    static final ObjectMapper mapper = new ObjectMapper();

    public StationServlet(StationRecordRetriever stationRecordRetriever) {
        this.stationRecordRetriever = stationRecordRetriever;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String stationId = null;

        if (req.getDispatcherType() == DispatcherType.REQUEST) {
            ArrayList<String> pathList = Api.getUrlPathList(req);
            if(pathList.size() > 0)
                stationId = pathList.get(0);

        }
        else if (req.getDispatcherType() == DispatcherType.FORWARD) {
            stationId = req.getParameter(PARAM_ID);
        }

        String format = req.getParameter(PARAM_FORMAT);

        if (FORMAT_CSV.equalsIgnoreCase(format))
            doGetCsvFormat(req, resp, stationId);
        else
            doGetJsonFormat(req, resp, stationId);


    }

    protected void doGetCsvFormat(HttpServletRequest req, HttpServletResponse resp, String stationId)
            throws ServletException, IOException
    {
        resp.setContentType(FORMAT_CSV_TYPE);

        PrintWriter out = resp.getWriter();
        if (stationId == null || stationId.isEmpty()) {
            resp.setContentLength(0);
            return;
        }

        StringBuilder output = new StringBuilder();
        List<String> list = stationRecordRetriever.getRawStationDataList(stationId);
        for (String line : list) {
            output.append(line).append('\n');
        }

        resp.setContentLength(output.length());

        out.print(output);
    }

    protected void doGetJsonFormat(HttpServletRequest req, HttpServletResponse resp, String stationId)
            throws ServletException, IOException
    {
        resp.setContentType(FORMAT_JSON_TYPE);

        PrintWriter out = resp.getWriter();
        List<StationData> stationDataList;
        if (stationId == null || stationId.isEmpty())
            stationDataList = List.of();
        else
            stationDataList = stationRecordRetriever.getStationDataList(stationId);

        mapper.writeValue(out, new QueryResults<StationData>(QueryResults.Type.STATION_DATA, stationDataList));
    }
}
