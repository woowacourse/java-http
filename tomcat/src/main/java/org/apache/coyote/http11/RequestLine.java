package org.apache.coyote.http11;

public class RequestLine {
    private final String method;
    private final String requestTarget;
    private final String protocolVersion;

    public RequestLine(String method, String requestTarget, String protocolVersion) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String readLine) {
        if (readLine == null) {
            return null;
        }
        String[] parts = splitRequestParts(readLine);
        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String getPath() {
        int queryIndex = requestTarget.indexOf('?');
        if (queryIndex >= 0) {
            return requestTarget.substring(0, queryIndex);
        }
        return requestTarget;
    }

    public String getQueryString() {
        int queryIndex = requestTarget.indexOf('?');
        if (queryIndex >= 0) {
            return requestTarget.substring(queryIndex + 1);
        }
        return "";
    }

    public String getMethod() {
        return method;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    private static String[] splitRequestParts(String readLine) {
        return readLine.split(" ");
    }

}
