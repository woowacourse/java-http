package org.apache.coyote;

public class Request {

    private static final String START_LINE_DELIMITER = " ";

    private String method;
    private String requestUrl;
    private String version;

    public Request(final String method, final String requestUrl, final String version) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public static Request from(final String startLine) {
        String[] line = startLine.split(START_LINE_DELIMITER);
        if (line.length != 3) {
            throw new HttpRequestStartLineNotValidException();
        }
        return new Request(line[0], line[1], line[2]);
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
}
