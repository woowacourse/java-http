package org.apache.coyote.http11;

public class StartLine {

    private final String method;
    private final String requestURI;
    private final String httpVersion;

    public static StartLine from(String rawStartLine) {
        String[] split = rawStartLine.split(" ");
        return new StartLine(split[0], split[1], split[2]);
    }

    private StartLine(String method, String requestURI, String httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public String absolutePath() {
        if (requestURI.contains("?")) {
            return requestURI.substring(0, requestURI.indexOf("?"));
        }
        return requestURI;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
