package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.StringJoiner;

public class Response {

    private static final String FORMAT_OF_RESPONSE_LINE = "HTTP/%.1f %s ";
    private static final String HTTP_MESSAGE_LINE_SEPARATOR = "\r\n";
    private static final String HTTP_MESSAGE_EMPTY_LINE = "";

    private final float version;
    private ResponseStatus status;
    private final ResponseHeaders headers;
    private final ResponseCookies cookies;
    private final ResponseBody body;

    public Response() {
        this.version = 1.1F;
        this.status = ResponseStatus.OK;
        this.headers = new ResponseHeaders();
        this.cookies = new ResponseCookies();
        this.body = new ResponseBody();
    }

    public void setStatusUnauthorized() {
        this.status = ResponseStatus.UNAUTHORIZED;
    }

    public void sendRedirection(String location) {
        this.status = ResponseStatus.FOUND;
        this.headers.addLocation(location);
    }

    public void addLoginCookie(String newSessionId) {
        cookies.addLoginCookie(newSessionId);
    }

    public void addFileBody(String filePath) throws URISyntaxException, IOException {
        body.addFile(filePath);
        headers.addContentTypeByFileExtension(filePath);
        headers.addContentLength(body.getLength());
    }

    public String buildHttpMessage() {
        StringJoiner messageJoiner = new StringJoiner(HTTP_MESSAGE_LINE_SEPARATOR);
        buildResponseLine(messageJoiner);
        headers.buildHttpMessage(messageJoiner);
        cookies.buildHttpMessage(messageJoiner);
        messageJoiner.add(HTTP_MESSAGE_EMPTY_LINE);
        body.buildHttpMessage(messageJoiner);
        return messageJoiner.toString();
    }

    private void buildResponseLine(StringJoiner messageJoiner) {
        messageJoiner.add(String.format(FORMAT_OF_RESPONSE_LINE,
                version,
                status.buildHttpMessage()));
    }
}
