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

            Request request = parseRequest(inputStream);
            String statusLine;
            String contentType;
            byte[] body;
            if (request == null) {
                statusLine = "HTTP/1.1 400 Bad Request ";
                contentType = "text/html;charset=utf-8";
                body = readPathFile("static/400.html");
            } else {
                String requestPath = createValidRequestPath(request.path());
                body = readPathFile(requestPath);
                if (body == null) {
                    statusLine = "HTTP/1.1 404 Not Found ";
                    contentType = "text/html;charset=utf-8";
                    body = readPathFile("static/404.html");
                } else {
                    statusLine = "HTTP/1.1 200 OK ";
                    contentType = guessContentType(requestPath);
                }
            }
            response(statusLine, contentType, body, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request parseRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String[] firstLine = readFirstLine(br);
        if (firstLine == null) {
            return null;
        }
        String method = firstLine[0];
        String path = firstLine[1];
        String version = firstLine[2];
        Map<String, String> headers = readHeaders(br);
        return new Request(method, path, version, headers);
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

    private Map<String, String> readHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            String[] splitLine = line.split(":");
            if (splitLine.length < 2) {
                continue;
            }
            String key = splitLine[0].trim();
            StringBuilder sb = new StringBuilder();
            for (int idx = 1; idx < splitLine.length; idx++) {
                sb.append(splitLine[idx]);
            }
            String value = sb.toString().trim();
            headers.put(key, value);
        }
        return headers;
    }

    private String createValidRequestPath(String path) {
        String resourcePath = "static";
        if (path.startsWith("/")) {
            resourcePath += path;
            return resourcePath;
        }
        return resourcePath + "/" + path;
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
}
