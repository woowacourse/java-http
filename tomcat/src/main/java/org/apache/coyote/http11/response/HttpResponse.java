package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpResponse {

    private final OutputStream outputStream;
    private final Map<String, List<String>> headers = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addHeader(String key, String value) {
        headers.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void setCookie(String name, String value) {
        addHeader("Set-Cookie", name + "=" + value);
    }

    public void send(HttpStatus status, String contentType, byte[] body) throws IOException {
        addHeader("Content-Type", contentType);
        addHeader("Content-Length", String.valueOf(body.length));
        writeResponse(status.getStatusLine(), body);
    }

    public void send(HttpStatus status) throws IOException {
        String defaultBody = status.getStatusCode() + " " + status.getReasonPhrase();
        byte[] bodyBytes = defaultBody.getBytes(StandardCharsets.UTF_8);
        String contentType = "text/plain;charset=utf-8";
        send(status, contentType, bodyBytes);
    }

    public void sendRedirect(String location) throws IOException {
        addHeader("Location", location);
        writeResponse(HttpStatus.FOUND.getStatusLine(), new byte[0]);
    }

    private void writeResponse(String statusLine, byte[] body) throws IOException {
        StringBuilder formattedHeader = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                formattedHeader.append(entry.getKey()).append(": ").append(value).append("\r\n");
            }
        }
        final var responseStart = "HTTP/1.1 " + statusLine + "\r\n"
                + formattedHeader + "\r\n";
        outputStream.write(responseStart.getBytes(StandardCharsets.UTF_8));
        if (body != null && body.length > 0) {
            outputStream.write(body);
        }
        outputStream.flush();
    }

    public Session addSession() {
        Manager sessionManager = SessionManager.getInstance();
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        setCookie("JSESSIONID", session.getId());
        return session;
    }
}
