package org.apache.coyote.http11.request;

public class RequestPath {

    private static final String DELIMITER_OF_REQUEST_PARAM = "\\?";
    private static final int INDEX_OF_PATH = 0;
    private static final int INDEX_OF_PARAMS = 1;

    private final String path;
    private RequestParams requestParams;

    public RequestPath(String uri) {
        String[] tokens = uri.split(DELIMITER_OF_REQUEST_PARAM);
        this.path = tokens[INDEX_OF_PATH];
        if (hasRequestParams(uri)) {
            this.requestParams = new RequestParams(tokens[INDEX_OF_PARAMS]);
        }
    }

    private boolean hasRequestParams(String uri) {
        return uri.contains(DELIMITER_OF_REQUEST_PARAM);
    }

    public boolean hasPath(String path) {
        return this.path.equals(path);
    }

    public String getPath() {
        return this.path;
    }
}
