package org.apache.coyote.http11.response;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final OutputStream outputStream;
    private final List<String> cookies = new ArrayList<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookie(String cookie) {
        cookies.add(cookie);
    }

    public void clearCookies() {
        cookies.clear();
    }

    public void redirect(String location) {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + HttpStatus.FOUND.getStatusLine());
        addCookies(lines);
        lines.add("Location: " + location);
        lines.add("Content-Length: 0");
        lines.add("");
        lines.add("");

        write(String.join("\r\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    public void sendError(HttpStatus status) {
        String errorPage = status.getErrorPage();
        if (errorPage == null) {
            send(status, "/index.html", new byte[0]);
            return;
        }

        send(status, errorPage, new byte[0]);
    }

    public void send(HttpStatus status, String resourcePath, byte[] body) {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + status.getStatusLine());
        addCookies(lines);
        lines.add("Content-Type: " + MimeType.from(resourcePath));
        lines.add("Content-Length: " + body.length);
        lines.add("");
        lines.add("");

        write(String.join("\r\n", lines).getBytes(StandardCharsets.UTF_8));
        write(body);
    }

    private void addCookies(List<String> lines) {
        for (String cookie : cookies) {
            lines.add("Set-Cookie: " + cookie);
        }
    }

    private void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}
