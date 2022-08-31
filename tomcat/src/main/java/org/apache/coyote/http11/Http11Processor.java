package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = Request.from(new String(inputStream.readAllBytes()));

            final var response = makeResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(final Request request) throws IOException {
        if (request.getMethod().equals(HttpMethod.GET)) {
            final var responseBody = makeResponseBody(request);
            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    private String makeResponseBody(final Request request) throws IOException {
        final var path = request.getPath();

        if (path.equals("/")) {
            return "Hello world!";
        }

        final var resource = getClass().getClassLoader().getResource("static" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
