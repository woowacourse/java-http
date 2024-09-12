package org.apache.coyote.http11.request;

public class RequestPath {

    private static final String DELIMITER_OF_REQUEST_PARAM = "\\?";

    private final String path;
    private final RequestParams requestParams;

    public RequestPath(String uri) {
        String[] tokens = uri.split(DELIMITER_OF_REQUEST_PARAM);
        this.path = tokens[0];
        this.requestParams = getRequestParams(tokens);
    }

    private RequestParams getRequestParams(String[] tokens) {
        if (tokens.length == 2) {
            return new RequestParams(tokens[1]);
        }
        return new RequestParams();
    }

    public boolean hasPath(String path) {
        return this.path.equals(path);
    }

    public String getPath() {
        return this.path;
    }
}
