package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(inputStream);
            log.info("request: {}", request);

            if (request.getMethod() == HttpMethod.GET) {
                byte[] resource = StaticResourceLoader.load(request.getUri());
                String extension = request.getUri().toString()
                        .substring(request.getUri().toString().lastIndexOf(".") + 1);
                String responseBody = new String(resource);
                HttpResponse response = HttpResponse.builder()
                        .ok()
                        .contentType(ContentType.fromExtension(extension))
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
