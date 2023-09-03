package org.apache.coyote.http11.request;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final RequestVersion requestVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestPath requestPath,
                       final RequestVersion requestVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.requestVersion = requestVersion;
    }

    public static RequestLine convert(String line) {
        final String[] splitLine = line.split(DELIMITER);
        final HttpMethod httpMethod = HttpMethod.findByName(splitLine[METHOD_INDEX]);
        final RequestPath requestPath = new RequestPath(splitLine[PATH_INDEX]);
        final RequestVersion requestVersion = new RequestVersion(splitLine[VERSION_INDEX]);
        return new RequestLine(httpMethod, requestPath, requestVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath.getPath();
    }

    public String getRequestVersion() {
        return requestVersion.getVersion();
    }
}
