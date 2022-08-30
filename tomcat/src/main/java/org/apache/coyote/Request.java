package org.apache.coyote;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

public class Request {

    private static final String START_LINE_DELIMITER = " ";

    private String method;
    private String requestUrl;
    private String version;
    private Headers headers;

    public Request(final BufferedReader bufferedReader) {
        List<String> requestLines = bufferedReader.lines()
                .collect(Collectors.toList());

        setStartLine(requestLines.remove(0));
        setHeaders(requestLines);
    }

    public Request(final String method, final String requestUrl, final String version, final Headers headers) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.version = version;
        this.headers = headers;
    }

    public void setStartLine(final String startLine) {
        String[] start = startLine.split(START_LINE_DELIMITER);
        method = start[0];
        requestUrl = start[1];
        version = start[2];
    }

    public void setHeaders(final List<String> rowHeaders) {
        headers = Headers.from(rowHeaders);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }

    public Headers getHeaders() {
        return headers;
    }
}
