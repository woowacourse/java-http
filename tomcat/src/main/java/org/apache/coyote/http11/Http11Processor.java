package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.ControllerMapper;
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
        try (
                final var bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                final var writer = connection.getOutputStream()) {
            Http11Request http11Request = parseRequest(bufferedReader);
            String path = http11Request.getPath();

            Http11Response http11Response = ControllerMapper.getInstance()
                    .getController(path)
                    .service(http11Request);
            writer.write(http11Response.getResponseHeader());
            writer.write(http11Response.getBody());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request parseRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        String[] splitRequestLine = requestLine.split(" ");

        if (splitRequestLine.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request line format");
        }

        String method = splitRequestLine[0];
        String targetResource = splitRequestLine[1];
        String protocol = splitRequestLine[2];

        Map<String, String> headers = parseHeaders(bufferedReader);

        String body = parseBody(bufferedReader, headers);

        return new Http11Request(method, targetResource, protocol, headers, body);
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(":");
            String header = split[0].trim();
            String value = split[1].trim();
            headers.put(header, value);
        }
        return headers;
    }

    private String parseBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String lenHeader = headers.get("Content-Length");
        if (lenHeader == null) {
            return "";
        }

        int len;
        try {
            len = Integer.parseInt(lenHeader);
        } catch (NumberFormatException e) {
            return "";
        }

        if (len <= 0) {
            return "";
        }

        char[] buf = new char[len];
        int read = 0;
        while (read < len) {
            int r = bufferedReader.read(buf, read, len - read);
            if (r == -1) {
                break;
            }
            read += r;
        }
        return new String(buf, 0, read);
    }
}
