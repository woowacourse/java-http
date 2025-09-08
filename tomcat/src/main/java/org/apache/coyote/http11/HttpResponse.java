package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private String httpVersion = "HTTP/1.1";
    private int status = 200;
    private String reasonPhrase = "OK";
    private final Map<String, List<String>> headers = new HashMap<>();
    private byte[] body = new byte[0];

    public void redirect(String location) {
        setStatus(302, "Found");
        setHeader("Location", location);
        setBody(new byte[0]);
    }

    public void writeText(String text, String contentType) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        setContentType(contentType);
        setBody(bytes);
    }

    public void writeResponse(OutputStream outputStream) throws IOException {
        headers.put("Content-Length", new ArrayList<>(List.of(String.valueOf(body.length))));

        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion).append(" ").append(status).append(" ").append(reasonPhrase).append("\r\n");
        for (Map.Entry<String, List<String>> e : headers.entrySet()) {
            String name = e.getKey();
            for (String v : e.getValue()) {
                sb.append(name).append(": ").append(v).append("\r\n");
            }
        }
        sb.append("\r\n");

        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        if (body.length > 0) {
            outputStream.write(body);
        }
        outputStream.flush();
    }

    public void setContentType(final String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void addSetCookie(final String setCookieValue) {
        addHeader("Set-Cookie", setCookieValue);
    }

    public void setStatus(final int status, final String reasonPhrase) {
        this.status = status;
        this.reasonPhrase = reasonPhrase;
    }

    public void setBody(final byte[] body) {
        if (body == null) {
            this.body = new byte[0];
        } else {
            this.body = body;
        }
    }

    private void setHeader(final String name, final String value) {
        headers.put(name, new ArrayList<>(List.of(value)));
    }

    private void addHeader(final String name, final String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }
}
