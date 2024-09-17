package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String DELIMITER_OF_COMPONENTS = " ";
    private static final String DELIMITER_OF_VERSION = "/";
    private static final int INDEX_OF_METHOD = 0;
    private static final int INDEX_OF_PATH = 1;
    private static final int INDEX_OF_VERSION = 2;
    private static final int INDEX_OF_VERSION_NUMBER = 1;

    private final RequestMethod method;
    private final RequestPath path;
    private final float version;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(DELIMITER_OF_COMPONENTS);
        this.method = RequestMethod.find(tokens[INDEX_OF_METHOD]);
        this.path = new RequestPath(tokens[INDEX_OF_PATH]);
        this.version = Float.parseFloat(tokens[INDEX_OF_VERSION].split(DELIMITER_OF_VERSION)[INDEX_OF_VERSION_NUMBER]);
    }

    public boolean hasPath(String path) {
        return this.path.hasPath(path);
    }

    public boolean hasGetMethod() {
        return this.method.isGet();
    }

    public boolean hasPostMethod() {
        return this.method.isPost();
    }

    public String getPath() {
        return path.getPath();
    }
}
