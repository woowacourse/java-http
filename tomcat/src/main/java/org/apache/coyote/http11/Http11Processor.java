package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    private static final String GET = "GET";
    private static final String DELIMITER = "\r\n";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final String REQUEST_API_DELIMITER = " ";
    private static final String STATIC_DIRECTORY = "static";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String line = bufferedReader.readLine();
            final String[] requestInfos = line.split(REQUEST_API_DELIMITER);

            final String httpMethod = requestInfos[HTTP_METHOD_INDEX];
            final String requestUri = requestInfos[REQUEST_URI_INDEX];

            if (httpMethod.equals(GET)) {
                final File file = readStaticFile(requestUri);
                final String responseBody = new String(Files.readAllBytes(file.toPath()));

                final var response = String.join(DELIMITER,
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File readStaticFile(final String requestUri) {
        final URL resource = classLoader.getResource(STATIC_DIRECTORY + requestUri);
        return new File(resource.getFile());
    }
}
