package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private DataOutputStream dos;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void forward(String url) {
        try (final var resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("static" + url)) {
            byte[] body = new byte[0];
            if (resourceStream != null) {
                body = resourceStream.readAllBytes();
            }
            headers.put("Content-Type", contentType(url));
            headers.put("Content-Length", body.length + " ");
            response200Header(body.length);
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void response200Header(int contentLength) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processHeaders() {
        try {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String contentType(final String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (requestTarget.endsWith(".svg")) {
            return "image/svg+xml ";
        }
        return "text/html;charset=utf-8 ";
    }
}
