package org.apache.catalina.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.catalina.http.HeaderName;
import org.apache.catalina.http.StatusCode;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader header;
    private final ResponseBody body;

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.header = new ResponseHeader();
        this.body = new ResponseBody();
    }

    public String getReponse() {
        StringBuilder response = new StringBuilder();

        response.append(statusLine.getResponse())
                .append("\r\n")
                .append(header.getResponse())
                .append("\r\n")
                .append(body.getContent());
        return String.valueOf(response);
    }

    public void setStatusCode(StatusCode statusCode) {
        statusLine.setStatusCode(statusCode);
    }

    public void addHeader(HeaderName headerName, String value) {
        header.addHeader(headerName, value);
    }

    public void setBody(String path, String contentType) {
        body.setBody(path);
        header.addHeader(HeaderName.CONTENT_LENGTH, String.valueOf(body.getLength()));
        header.addHeader(HeaderName.CONTENT_TYPE, contentType);
    }

    public void setBody(String resource) {
        body.setBody(resource);
        header.addHeader(HeaderName.CONTENT_LENGTH, String.valueOf(body.getLength()));
        String contentType;
        try {
            contentType = Files.probeContentType(new File(resource).toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Not found path");
        }
        header.addHeader(HeaderName.CONTENT_TYPE, contentType);
    }

    public void addSession(String sessionId) {
        header.addSession(sessionId);
    }
}
