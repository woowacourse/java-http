package org.apache.coyote.http.response;

public class ResponseLine {

    private final String protocolVersion;
    private final int statusCode;
    private final String statusText;

    public ResponseLine(String protocolVersion, int statusCode, String statusText) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static ResponseLine ok() {
        return new ResponseLine("HTTP/1.1", 200, "OK");
    }

    public static ResponseLine found() {
        return new ResponseLine("HTTP/1.1", 302, "Found");
    }

    public static ResponseLine unauthorized() {
        return new ResponseLine("HTTP/1.1", 401, "Unauthorized");
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    @Override
    public String toString() {
        return protocolVersion + " " + statusCode + " " + statusText;
    }
}
