package org.apache.coyote.http.request;

public class HttpRequestUri {

    private static final String PATH_PARAMETER_DELIMITER = "\\?";

    private final String path;
    private final HttpParameters parameters;

    private HttpRequestUri(String path, HttpParameters parameters) {
        this.path = path;
        this.parameters = parameters;
    }

    public static HttpRequestUri from(String requestUriString) {
        String[] split = requestUriString.split(PATH_PARAMETER_DELIMITER);

        String queryString = "";
        if (1 < split.length) {
            queryString = split[1];
        }

        return new HttpRequestUri(
            decodePath(split[0]),
            HttpParameters.from(queryString)
        );
    }

    private static String decodePath(String path) {
        if (path.equals("/")) {
            return "/home";
        }
        return path;
    }

    public String getPath() {
        return path;
    }

    public HttpParameters getParameters() {
        return parameters;
    }
}
