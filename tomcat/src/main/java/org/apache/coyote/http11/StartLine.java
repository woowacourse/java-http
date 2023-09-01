package org.apache.coyote.http11;

public class StartLine {

    private final String method;
    private final String requestURI;
    private final String httpVersion;

    public static StartLine from(String startLine) {
        String[] split = startLine.split(" ");
        return new StartLine(split[0], split[1], split[2]);
    }

    private StartLine(String method, String requestURI, String httpVersion) {
        this.method = method;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public AbsolutePath absolutePath() {
        if (requestURI.contains("?")) {
            return AbsolutePath.from(requestURI.substring(0, requestURI.indexOf("?")));
        }
        return AbsolutePath.from(requestURI);
    }

    public String[] queryParameters() {
        return requestURI.substring(requestURI.indexOf("?") + 1)
                .split("&");
    }

    public boolean hasQueryParameters() {
        return requestURI.contains("?");
    }
}
