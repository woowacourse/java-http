package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String DELIMITER_OF_COMPONENTS = " ";
    private static final String DELIMITER_OF_VERSION = "/";

    private final RequestMethod method;
    private final RequestPath path;
    private final float version;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(DELIMITER_OF_COMPONENTS);
        this.method = RequestMethod.find(tokens[0]);
        this.path = new RequestPath(tokens[1]);
        this.version = Float.parseFloat(tokens[2].split(DELIMITER_OF_VERSION)[1]);
    }

    public boolean hasPath(String path) {
        return this.path.hasPath(path);
    }

    public boolean hasMethod(RequestMethod method) {
        return this.method == method;
    }

    public String getPath() {
        return path.getPath();
    }
}
