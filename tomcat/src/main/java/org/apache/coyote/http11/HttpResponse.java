package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class HttpResponse {

    private StatusLine statusLine;
    private HttpHeaders httpHeaders;
    private String body;

    public HttpResponse() {
        statusLine = new StatusLine(HttpStatus.OK);
        httpHeaders = new HttpHeaders(Collections.emptyMap());
        body = "";
    }

    public String makeResponse() {
        StringBuilder sb = new StringBuilder();

        sb.append(statusLine.getStatusLine()).append(" \r\n")
                .append(httpHeaders.toTextHeader()).append("\r\n")
                .append(body);

        return sb.toString();
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.statusLine = new StatusLine(httpStatus);
    }

    public void setBody(final String value) {
        body += value;
        httpHeaders.setContentType("text/html");
        httpHeaders.setContentLength(body.length());
    }

    public void setBody(final String value, final String contentType, final int contentLength) {
        body += value;
        httpHeaders.setContentType(contentType);
        httpHeaders.setContentLength(contentLength);
    }

    public void setView(final String viewName) {
        URI uri = null;
        try {
            uri = getClass().getClassLoader().getResource("static/" + viewName + ".html").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        final Path path = Paths.get(uri);

        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setBody(new String(bytes), contentType, bytes.length);
    }
}
