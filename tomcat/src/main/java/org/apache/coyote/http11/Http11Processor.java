package org.apache.coyote.http11;

import javassist.NotFoundException;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerMapping controllerMapping = new ControllerMapping();

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while (!(line = bufferedReader.readLine()).isBlank()) {
                if (isStartsWithRequestMethod(line)) {
                    final String uri = line.split(" ")[1];
                    final Controller controller = controllerMapping.getController(uri);
                    final String responseBody = controller.execute(uri);
                    final String response = makeResponse(responseBody);
                    flushResponse(outputStream, response);
                    return;
                }
            }

        } catch (NotFoundException | URISyntaxException e) {
            log.info(e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private boolean isStartsWithRequestMethod(final String line) {
        for (String requestMethod : List.of("GET", "POST", "PUT", "PATCH", "DELETE")) {
            if (line.startsWith(requestMethod)) {
                return true;
            }
        }
        return false;
    }

    private void flushResponse(final OutputStream outputStream, final String responseBody) throws IOException {
        outputStream.write(responseBody.getBytes());
        outputStream.flush();
    }

    private String makeResponse(final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
