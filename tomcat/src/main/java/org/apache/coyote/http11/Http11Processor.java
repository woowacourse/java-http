package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
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

            final var request = createRequest(inputStream);
            log.info("[REQUEST] = {}", request);
            final var responseBody = createResponseBody(request.getUri());

            Map<String, String> header = new LinkedHashMap<>();
            header.put("Content-Type", ContentType.HTML.getValue() + ";" + "charset=utf-8 ");
            header.put("Content-Length", responseBody.getBytes().length + " ");
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, header, responseBody);

            final var response = httpResponse.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponseBody(String uri) throws IOException {
        URL resource = Http11Processor.class.getClassLoader().getResource(uri);
        if (resource == null) {
            log.warn("Bad Request uri = {}", uri);
            throw new IllegalArgumentException("No Resource Exception");
        }
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private HttpRequest createRequest(InputStream inputStream) {
        try {
            byte[] bytes = new byte[18000]; // TODO refactor
            int readByteCount = inputStream.read(bytes);
            String requestString = new String(bytes, 0, readByteCount, StandardCharsets.UTF_8);
            return HttpRequestParser.parse(requestString);
        } catch (IOException e) {
            log.error("IO Exception occur during make request object");
        }
        return null;
    }
}
