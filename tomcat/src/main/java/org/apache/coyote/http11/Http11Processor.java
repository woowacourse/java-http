package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(input);

            if(request.hasFilePath()) {
                File requestFile = getRequestFile(request);
                String mimeType = Files.probeContentType(requestFile.toPath());
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: %s;charset=utf-8 ".formatted(mimeType),
                        "Content-Length: " + requestFile.toString().getBytes().length + " ",
                        "",
                        requestFile.toString());
                outputStream.write(response.getBytes());
            } else {
                FrontController frontController = FrontController.getInstance();
                Controller controller = frontController.mapController(request.getMethod(), request.getPath());
                controller.service(request);
            }
            outputStream.flush();

        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File getRequestFile(HttpRequest httpRequest) throws IOException {
        String resourcePath = "static" + httpRequest.getPath();
        URL resource = getClass().getClassLoader().getResource(resourcePath);

        return new File(resource.getPath());
    }
}
