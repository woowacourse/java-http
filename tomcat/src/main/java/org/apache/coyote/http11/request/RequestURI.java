package org.apache.coyote.http11.request;

public class RequestURI {

    private static final String QUERY_DELIMITER = "?";

    private final String path;
    private final QueryParameters parameters;

    public RequestURI(String path, QueryParameters parameters) {
        this.path = path;
        this.parameters = parameters;
    }

    public RequestURI(String requestURI) {
        if(requestURI.contains(QUERY_DELIMITER)) {
            int index = requestURI.indexOf(QUERY_DELIMITER);
            path = requestURI.substring(0, index);
            parameters = new QueryParameters(requestURI.substring(index + 1));
            return;
        }
        path = requestURI;
        parameters = new QueryParameters("");
    }

    public String getPath() {
        return path;
    }
}
