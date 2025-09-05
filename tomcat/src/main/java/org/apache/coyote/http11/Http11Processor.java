package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = parseRequest(inputStream);
            String statusLine;
            String contentType;
            byte[] body;

            if (httpRequest == null) {
                statusLine = "HTTP/1.1 200 OK ";
                contentType = "text/html;charset=utf-8";
                body = "Hello world!".getBytes();
                response(statusLine, contentType, body, outputStream);
                return;
            }
            String requestPath = createValidRequestPath(httpRequest.path());
            if (requestPath == null) {
                statusLine = "HTTP/1.1 200 OK ";
                contentType = "text/html;charset=utf-8";
                body = "Hello world!".getBytes();
                response(statusLine, contentType, body, outputStream);
                return;
            }
            body = readPathFile(requestPath);
            if (body == null) {
                statusLine = "HTTP/1.1 404 Not Found ";
                contentType = "text/html;charset=utf-8";
                body = readPathFile("static/404.html");
                response(statusLine, contentType, body, outputStream);
                return;
            }
            statusLine = "HTTP/1.1 200 OK ";
            contentType = guessContentType(requestPath);
            response(statusLine, contentType, body, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstLine = readFirstLine(br);
        if (firstLine == null) {
            return null;
        }
        String method = firstLine[0];
        String path = parsePath(firstLine[1]);
        Map<String, String> queries = parseQueries(firstLine[1]);
        String version = firstLine[2];
        return new HttpRequest(method, path, version, queries);
    }

    private String[] readFirstLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null || line.isEmpty()) {
            return null;
        }
        String[] firstLine = line.split(" ", 3);
        if (firstLine.length != 3) {
            return null;
        }
        return firstLine;
    }

    private String parsePath(String path) {
        String[] splitPath = path.split("[?]");
        return splitPath[0];
    }

    private Map<String, String> parseQueries(String path) throws IOException {
        String[] splitPath = path.split("[?]");
        if (splitPath.length != 2) {
            return null;
        }
        String query = splitPath[1];
        Map<String, String> queries = new HashMap<>();
        for (String pair : query.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            String key;
            String value = "";
            int separatorIdx = pair.indexOf('=');
            if (separatorIdx >= 0) {
                key = pair.substring(0, separatorIdx);
                value = pair.substring(separatorIdx + 1);
            } else {
                key = pair;
            }
            queries.put(key, value);
        }
        return queries;
    }

    private String createValidRequestPath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return null;
        }
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path += "index.html";
        }
        String last = path.substring(path.lastIndexOf('/') + 1);
        if (!last.contains(".")) {
            path += ".html";
        }
        return "static" + path;
    }

    private byte[] readPathFile(String requestPath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private String guessContentType(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (lower.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (lower.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "application/octet-stream";
    }

    private void response(String statusLine, String contentType, byte[] body, OutputStream outputStream)
            throws IOException {
        String header = String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "", ""
        );
        outputStream.write(header.getBytes());
        outputStream.write(body);
        outputStream.flush();
    }
}
