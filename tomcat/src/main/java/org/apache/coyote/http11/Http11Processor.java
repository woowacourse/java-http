package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            final String requestValue = readHttpRequest(inputStream);
            if (requestValue == null) {
                return;
            }

            final String resource = requestValue.split(" ")[1];

            String contentType = "text/html";

            if (resource.contains("/css")) {
                contentType = "text/css";
            }

            final String responseBody = makeResponseBody(resource);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String readHttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final String requestValue = br.readLine();
        return requestValue;
    }

    public String makeResponseBody(final String resource) throws IOException, URISyntaxException {
        if ("/".equals(resource)) {
            return "Hello world!";
        }
        final URI uri = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + resource))
                .toURI();
        return new String(Files.readAllBytes(Paths.get(uri)));
    }
}
