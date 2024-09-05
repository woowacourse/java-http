package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader classLoader = Http11Processor.class.getClassLoader();

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(inputStream);
            log.info("request: {}", request);

            if (request.getMethod() == HttpMethod.GET) {
                String indexFileName = classLoader.getResource("static/" + request.getUri()).getFile();
                String responseBody = new String(Files.readAllBytes(Paths.get(indexFileName)));
                HttpResponse response = HttpResponse.builder()
                        .ok()
                        .contentType(ContentType.TEXT_HTML)
                        .body(responseBody)
                        .build();
                outputStream.write(HttpResponseWriter.write(response).getBytes());
                outputStream.flush();
                return;
            }
            HttpResponse response = HttpResponse.builder()
                    .statusCode(StatusCode.METHOD_NOT_ALLOWED)
                    .build();
            outputStream.write(HttpResponseWriter.write(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
