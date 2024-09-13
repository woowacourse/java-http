package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    public static final String SEPARATOR = " ";
    public static final int METHOD_INDEX = 0;
    public static final int PATH_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;

    private final String method;
    private final String path;
    private final String httpVersion;

    public RequestLine(final String method, final String path, final String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            return new RequestLine(null, null, null);
        }
        String[] elements = requestLine.split(SEPARATOR);
        return new RequestLine(elements[METHOD_INDEX], elements[PATH_INDEX], elements[HTTP_VERSION_INDEX]);
    }

    public boolean isNull() {
        return isNull(method) || isNull(path) || isNull(httpVersion);
    }

    private boolean isNull(final String value) {
        return value == null || value.isBlank();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
