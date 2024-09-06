package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

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

            InputReader inputReader = new InputReader(inputStream);
            HttpRequest request = new HttpRequest(inputReader);

            Controller controller = ControllerMapper.map(request.getPath());
            String viewName = controller.process(request.getUri());

            String responseBody = FileReader.read("static" + viewName);
            String contentType = Files.probeContentType(Path.of("static" + viewName));
            String response = createResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
