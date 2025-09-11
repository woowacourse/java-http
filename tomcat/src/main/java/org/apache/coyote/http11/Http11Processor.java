package org.apache.coyote.http11;

import com.techcourse.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
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
            RequestMapping requestMapping = RequestMapping.getInstance();

            HttpRequest request = getHttpRequest(inputStream);
            HttpResponse response = requestMapping.request(request);

            respond(response, outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return new HttpRequest(reader);
    }

    private void respond(HttpResponse response, OutputStream outputStream) throws IOException, URISyntaxException {
        final var responseText = response.createString();
        outputStream.write(responseText.getBytes());
        outputStream.flush();
    }
}
