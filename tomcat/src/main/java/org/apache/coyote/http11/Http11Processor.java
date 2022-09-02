package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_URI = "/";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String startLine = bufferedReader.readLine();
            if (startLine == null) {
                return;
            }

            if (RequestMethod.isIn(startLine)) {
                log.info(startLine);
                final String uri = startLine.split(" ")[1];
                final URL resource = getClass().getClassLoader().getResource("static/" + uri);

                String responseBody = "";
                if (uri.equals(DEFAULT_URI)) {
                    responseBody = "Hello world!";
                } else {
                    final Path path = new File(Objects.requireNonNull(resource).getPath()).toPath();
                    final byte[] bytes = Files.readAllBytes(path);
                    responseBody = new String(bytes);
                }

                final Response response = new Response(HttpStatus.OK, TEXT_HTML_CHARSET_UTF_8, responseBody);
                final String httpResponse = response.toHttpResponse();

                outputStream.write(httpResponse.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
