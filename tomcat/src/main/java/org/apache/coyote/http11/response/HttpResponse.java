package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private StatusLine statusLine;
    private Map<String, String> headers;
    private String responseBody;

    public HttpResponse() {
        this.headers = new LinkedHashMap<>();
    }

    public void setStatus200() {
        statusLine = new StatusLine("HTTP/1.1", "200", "OK");
    }

    public void setStatus404() {
        statusLine = new StatusLine("HTTP/1.1", "404", "Not Found");
    }

    public void setStatus302() {
        statusLine = new StatusLine("HTTP/1.1", "302", "Found");
    }

    public void setContentTypeHtml() {
        headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public void setContentTypeCss() {
        headers.put(CONTENT_TYPE, "text/css");
    }

    public void setResponseBodyByPath(Path path) throws IOException {
        responseBody = new String(Files.readAllBytes(path));
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    public void setResponseBodyByText(String content) {
        responseBody = content;
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    public String getResponse() {
        StringBuilder header = new StringBuilder();
        if (headers.containsKey(CONTENT_TYPE)) {
            header.append(CONTENT_TYPE + ": ").append(headers.get(CONTENT_TYPE) + " ").append(LINE_SEPARATOR);
        }
        if (headers.containsKey(CONTENT_LENGTH)) {
            header.append(CONTENT_LENGTH + ": ").append(headers.get(CONTENT_LENGTH) + " ").append(LINE_SEPARATOR);
        }

        return String.join(LINE_SEPARATOR,
                statusLine.getStatusLine(),
                header.toString(),
                responseBody
        );
    }
}
