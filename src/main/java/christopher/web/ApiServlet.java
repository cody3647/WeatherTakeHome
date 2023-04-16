package christopher.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet to direct parameter queries to the appropriate api query handler
 */
public class ApiServlet extends HttpServlet {
    final static public String NAME = "api";
    final static public String PARAM_QUERY = "query";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter(PARAM_QUERY);
        if(query == null)
            return;

        if(query.equals(Api.STATIONS)) {
            getServletContext().getNamedDispatcher(StationServlet.NAME).forward(req, resp);
            return;
        }
    }
}
