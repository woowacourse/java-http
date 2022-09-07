package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.apache.coyote.StatusLine;
import org.apache.coyote.http11.utill.FileUtils;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private StatusLine statusLine;
    private Headers headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.headers = new Headers();
        this.body = "";
    }

    public void addStatusCode(final StatusCode statusCode) {
        statusLine.addStatusCode(statusCode);
    }

    public void addBody(final String body) {
        this.headers = generateHeaders(ContentType.HTML, body);
        this.body = body;
    }

    public void addView(final String fileName) {
        String fileExtension = FileUtils.getFileExtension(fileName);
        ContentType contentType = ContentType.from(fileExtension);
        String responseBody = FileUtils.readFile(fileName);

        this.headers = generateHeaders(contentType, responseBody);
        this.body = responseBody;
    }

    private Headers generateHeaders(final ContentType contentType, final String body) {
        Headers headers = new Headers();
        headers.addHeader(CONTENT_TYPE, contentType.getValue());
        headers.addHeader(CONTENT_LENGTH, getContentLength(body));
        return headers;
    }

    private String getContentLength(final String body) {
        int contentLength = body.getBytes().length;
        return String.valueOf(contentLength);
    }

    public void addCookie(final String cookie) {
        headers.addHeader(SET_COOKIE, cookie);
    }

    public void sendRedirect(final String path) {
        this.addStatusCode(StatusCode.FOUND);
        headers.addHeader(LOCATION, path);
    }

    public String parseResponse() {
        StringBuilder headers = new StringBuilder();
        LinkedHashMap<String, String> values = this.headers.getValues();
        for (Entry<String, String> header : values.entrySet()) {
            headers.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }

        return statusLine.getStatusLineMessage() + " \r\n" +
                headers +
                "\r\n" +
                body;
    }
}
