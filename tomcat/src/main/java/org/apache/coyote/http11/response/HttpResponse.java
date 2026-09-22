package org.apache.coyote.http11.response;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

    public void sendStaticResource(String resourcePath) {
        URL resource = findResource(resourcePath);

        if (resource == null) {
            sendError(HttpStatus.NOT_FOUND);
            return;
        }

        byte[] body = readBody(resource);
        send(HttpStatus.OK, resourcePath, body);
    }

    public void sendError(HttpStatus status) {
        String errorPage = status.getErrorPage();
        if (errorPage == null) {
            send(status, "/index.html", new byte[0]);
            return;
        }

        URL resource = findResource(errorPage);
        if (resource == null) {
            send(status, errorPage, new byte[0]);
            return;
        }

        send(status, errorPage, readBody(resource));
    }

    private void send(HttpStatus status, String resourcePath, byte[] body) {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + status.getStatusLine());
        addCookies(lines);
        lines.add("Content-Type: " + getContentType(resourcePath));
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

    private URL findResource(String resourcePath) {
        return getClass()
                .getClassLoader()
                .getResource("static" + resourcePath);
    }

    private byte[] readBody(URL resource) {
        try (InputStream resourceStream = resource.openStream()) {
            return resourceStream.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
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
