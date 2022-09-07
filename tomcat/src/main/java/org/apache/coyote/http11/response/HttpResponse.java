package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.http.StatusCode;

public class HttpResponse {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String DEFAULT_RESOURCE_PACKAGE = "static";

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private String responseBody;

    public HttpResponse() {
    }

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, String responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public void send(String fileLocation, StatusCode statusCode) {
        statusLine = new StatusLine(DEFAULT_HTTP_VERSION, statusCode.getNumber(), statusCode.getName());
        responseBody = readFile(fileLocation);
    }

    protected String readFile(final String fileLocation) {
        try {
            Path filePath = new File(
                    getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PACKAGE + fileLocation)
                            .getFile()
            ).toPath();
            return Files.readString(filePath);
        } catch (NullPointerException | IOException e) {
            return DEFAULT_RESPONSE_BODY;
        }
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        if (responseBody == null) {
            return "";
        }
        return responseBody;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setResponseHeaders(ResponseHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
