package org.apache.coyote.http11;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpRequestParser;
import com.techcourse.http.MimeType;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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
             final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = HttpRequestParser.parse(inputStream);
            final String response = generateResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(HttpRequest request) {
        try {
            String uri = request.getUri();
            if ("/".equals(uri)) {
                return rootPage();
            }
            return getStaticResource(uri);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return "HTTP/1.1 404 Not Found ";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "HTTP/1.1 500 Internal Server Error ";
        }
    }

    private String rootPage() {
        return """
                HTTP/1.1 200 OK \r
                Content-Type: text/html;charset=utf-8 \r
                Content-Length: 12 \r
                \r
                Hello world!""";
    }

    private String getStaticResource(String requestUri) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found");
        }

        final Path path = Path.of(resource.getPath());
        final String responseBody = new String(Files.readAllBytes(path));
        String endUri = requestUri.substring(requestUri.lastIndexOf("/") + 1);

        return String.join(
                CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + MimeType.getMimeType(endUri) + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
