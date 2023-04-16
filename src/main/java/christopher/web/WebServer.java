package christopher.web;

import christopher.Main;
import christopher.datamanagement.StationRecordRetriever;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class WebServer {
    Server server;
    StationRecordRetriever stationRecordRetriever;
    ServletContextHandler contextHandler;
    public WebServer(StationRecordRetriever stationRecordRetriever) throws Exception {
        this.stationRecordRetriever = stationRecordRetriever;

        contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        server = new Server(8080);
        server.setHandler(contextHandler);

        setupStaticDir();
        setupApiHandlers();

    }

    public void start() throws Exception {

        server.start();
        server.join();
    }

    void setupStaticDir() throws IOException, URISyntaxException {
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(Resource.newResource(getStaticFileDirectory()));

        ServletHolder holder = new ServletHolder("default", DefaultServlet.class);
        holder.setInitParameter("dirAllowed", "true");
        contextHandler.addServlet(holder, "/");
    }

    void setupApiHandlers() {
        contextHandler.addServlet(new ServletHolder(ApiServlet.NAME, new ApiServlet()), "/" + Api.ENTRYPOINT + "/*");
        contextHandler.addServlet(new ServletHolder(StationServlet.NAME, new StationServlet(stationRecordRetriever)),
                                  "/" + Api.ENTRYPOINT + "/" + Api.STATIONS + "/*");
    }

    String getStaticFileDirectory() throws URISyntaxException {
        URL staticFile = Main.class.getClassLoader().getResource("christopher/site-root/index.html");
        if (staticFile == null) {
            throw new RuntimeException("Unable to find static resource directory");
        }

        return staticFile.toURI().toString().replace("index.html", "");
    }
}
