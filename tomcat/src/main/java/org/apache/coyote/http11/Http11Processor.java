package org.apache.coyote.http11;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticFileController;
import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final List<Controller> controllers = List.of(
            new HomeController(),
            new StaticFileController(),
            new LoginController(),
            new RegisterController()
    );

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
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String rawHttpRequest = readRawHttpRequest(bufferedReader);
            handle(HttpRequest.from(rawHttpRequest), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRawHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        int contentLength = headerLines.stream()
                .filter(l -> l.toLowerCase()
                        .startsWith("content-length"))
                .map(l -> Integer.parseInt(l.split(":")[1].trim()))
                .findFirst()
                .orElse(0);

        char[] body = new char[contentLength];
        if (contentLength > 0) {
            bufferedReader.read(body, 0, contentLength);
        }

        return String.join("\r\n", headerLines)
                + "\r\n\r\n"
                + new String(body);
    }

    private void handle(final HttpRequest httpRequest, final OutputStream outputStream) {
        for (Controller handler : controllers) {
            if (handler.support(httpRequest)) {
                HttpResponse httpResponse = HttpResponse.defaultHttpResponse(HttpVersion.ONE_ONE);

                try {
                    handler.service(httpRequest, httpResponse);

                    byte[] responseBytes = httpResponse.buildHttpResponse()
                            .getBytes(StandardCharsets.UTF_8);

                    outputStream.write(responseBytes);
                    outputStream.flush();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                return;
            }
        }
    }
}
