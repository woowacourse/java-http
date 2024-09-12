package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String url;
    private final String version;

    public RequestLine(String rawRequestLine) {
        String[] requestPart = rawRequestLine.split(" ");
        validateRequestPartLength(requestPart);
        this.method = requestPart[0];
        this.url = requestPart[1];
        this.version = requestPart[2];
    }

    private void validateRequestPartLength(String[] requestPart) {
        if (requestPart.length != 3) {
            throw new RuntimeException("RequestLine 양식이 맞지 않습다.");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
