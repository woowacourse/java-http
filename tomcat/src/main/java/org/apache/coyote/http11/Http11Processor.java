package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Controller controller = FrontController.getInstance();

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
            final var input = input(inputStream);
            final var request = new HttpRequest(input);
            final var response = controller.handle(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String input(final InputStream inputStream) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        var stringBuilder = new StringBuilder();
        var buffer = new char[inputStream.available()];
        int readCount = bufferedReader.read(buffer, 0, inputStream.available());
        stringBuilder.append(buffer, 0, readCount);
        return stringBuilder.toString();
    }
}
