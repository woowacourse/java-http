package org.apache.coyote.http11;

public class StartLine {

    private final String method;
    private final RequestURI requestURI;
    private final String httpVersion;

    public static StartLine from(String startLine) {
        String[] split = startLine.split(" ");
        return new StartLine(split[0], split[1], split[2]);
    }

    private StartLine(String method, String requestURI, String httpVersion) {
        this.method = method;
        this.requestURI = RequestURI.from(requestURI);
        this.httpVersion = httpVersion;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }
}
