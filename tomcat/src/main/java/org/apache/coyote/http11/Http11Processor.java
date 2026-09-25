package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            String line = reader.readLine();
            if (line == null) {
                return;
            }

            RequestLine requestLine = RequestLine.from(line);
            HttpHeaders headers = readHeaders(reader);
            String body = readBody(reader, headers);

            HttpRequest request = new HttpRequest(requestLine, headers, body);

            RequestMapping requestMapping = new RequestMapping();

            Controller controller = requestMapping.getController(request);
            HttpResponse response = controller.service(request);

            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            headers.put(
                    parts[0].trim().toLowerCase(),
                    parts[1].trim()
            );
        }
        return new HttpHeaders(headers);
    }

    private String readBody(BufferedReader reader, HttpHeaders headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));

        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                break;
            }

            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }
}
