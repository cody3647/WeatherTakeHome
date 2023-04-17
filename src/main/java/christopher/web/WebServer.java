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

    /**
     * Setup the Jetty Server
     * @param stationRecordRetriever StationRecordRetriever that has the data for the api
     * @throws Exception when the Server encounters an error
     */
    public WebServer(StationRecordRetriever stationRecordRetriever) throws Exception {
        this.stationRecordRetriever = stationRecordRetriever;

        contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        server = new Server(8080);
        server.setHandler(contextHandler);

        setupStaticDir();
        setupApiHandlers();

    }

    /**
     * Start the server
     * @throws Exception if there is an error running the server
     */
    public void start() throws Exception {

        server.start();
    //    server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    /**
     * Set up the directory to server static files from
     * @throws IOException if there is an error accessing the static directory
     * @throws URISyntaxException if there is an error forming the path to the static directory
     */
    void setupStaticDir() throws IOException, URISyntaxException {
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(Resource.newResource(getStaticFileDirectory()));

        ServletHolder holder = new ServletHolder("default", DefaultServlet.class);
        holder.setInitParameter("dirAllowed", "true");
        contextHandler.addServlet(holder, "/");
    }

    /**
     * Set up the api route handlers
     */
    void setupApiHandlers() {
        contextHandler.addServlet(new ServletHolder(ApiServlet.NAME, new ApiServlet()), "/" + Api.ENTRYPOINT + "/*");
        contextHandler.addServlet(new ServletHolder(StationServlet.NAME, new StationServlet(stationRecordRetriever)),
                                  "/" + Api.ENTRYPOINT + "/" + Api.STATIONS + "/*");
    }

    /**
     * Get the static directory as a String
     * @return String of the path to the static directory
     * @throws URISyntaxException if there is an error forming the path to the static directory
     */
    String getStaticFileDirectory() throws URISyntaxException {
        URL staticFile = Main.class.getClassLoader().getResource("christopher/site-root/index.html");
        if (staticFile == null) {
            throw new RuntimeException("Unable to find static resource directory");
        }

        return staticFile.toURI().toString().replace("index.html", "");
    }
}
