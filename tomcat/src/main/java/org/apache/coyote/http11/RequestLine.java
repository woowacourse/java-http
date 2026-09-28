package org.apache.coyote.http11;

public class RequestLine {
    private final String method;
    private final String requestTarget;
    private final String version;

    public RequestLine(String line) {
        final String[] requestLineParts = line.split(" ");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("잘못된 HTTP Request Line입니다: " + line);
        }
        method = requestLineParts[0];
        requestTarget = requestLineParts[1];
        version = requestLineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        final int querySeparatorIndex = requestTarget.indexOf("?");

        if (querySeparatorIndex == -1) {
            return requestTarget;
        }

        return requestTarget.substring(0, querySeparatorIndex);
    }

    public String getQueryString() {
        final int querySeparatorIndex = requestTarget.indexOf("?");

        if (querySeparatorIndex == -1) {
            return "";
        }

        return requestTarget.substring(querySeparatorIndex + 1);
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getVersion() {
        return version;
    }
}
