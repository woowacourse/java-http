package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> request = new ArrayList<>();
            while (bufferedReader.ready()) {
                request.add(bufferedReader.readLine());
            }

            HttpRequest httpRequest = new HttpRequest(request);
            String requestURI = httpRequest.getRequestURI();

            if (isRoot(requestURI)) {
                String responseBody = "Hello world!";
                String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }


            ResourceLocator resourceLocator = new ResourceLocator();
            File resource = resourceLocator.findResource(requestURI.substring(requestURI.lastIndexOf("/")));
            String extension = requestURI.substring(requestURI.lastIndexOf(".") + 1);

            String responseBody = new String(Files.readAllBytes(resource.toPath()));
            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + extension + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isRoot(String requestURI) {
        return "/".equals(requestURI);
    }
}
