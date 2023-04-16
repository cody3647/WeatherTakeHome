package christopher.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Helper class for Api handlers
 */
public class Api {
    static public String ENTRYPOINT = "api";
    static public String STATIONS = "stations";

    /**
     * Helper function to get a list of the parts of pathInfo from HttpServletRequest
     *
     * @param req HttpServletRequest to get url parts from
     * @return ArrayList of the strings between '/' in the request url.
     */
    static public ArrayList<String> getUrlPathList(HttpServletRequest req) {
        ArrayList<String> pathList = new ArrayList<>();
        String path = req.getPathInfo();
        if (path == null)
            return pathList;

        String[] pathPieces = path.split("/");

        // Skip the first element, if there is path info it starts with a '/'
        // and the first element will be empty string
        if(pathPieces.length > 1)
            pathList.addAll(Arrays.asList(pathPieces).subList(1, pathPieces.length));

        return pathList;
    }
}
