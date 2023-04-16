package christopher.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ApiServlet extends HttpServlet {
    final static public String NAME = "api";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.print("Api api");
        String query = req.getParameter("query");
        if(query == null)
            return;

        if(query.equals("station")) {
            getServletContext().getNamedDispatcher(StationServlet.NAME).forward(req, resp);
            return;
        }
    }
}
