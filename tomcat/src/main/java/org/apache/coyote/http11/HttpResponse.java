package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private StatusLine statusLine = new StatusLine(200, "OK");
    private final ResponseHeaders headers = new ResponseHeaders();
    private String contentType = "text/html";
    private ResponseBody body = new ResponseBody("");

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void sendRedirect(String location) {
        this.statusLine = new StatusLine(302, "Found");
        headers.add("Location", location);
    }

    public void setBody(StaticResource resource) {
        this.contentType = resource.getContentType();
        this.body = new ResponseBody(resource.getContent());
    }

    public byte[] getBytes() {
        headers.add("Content-Type", contentType + ";charset=utf-8");
        headers.add("Content-Length", String.valueOf(body.getContentLength()));

        List<String> lines = new ArrayList<>();
        lines.add(statusLine.toMessage());
        lines.addAll(headers.toLines());
        lines.add("");
        lines.add(body.getContent());

        return String.join("\r\n", lines).getBytes(StandardCharsets.UTF_8);
    }
}
