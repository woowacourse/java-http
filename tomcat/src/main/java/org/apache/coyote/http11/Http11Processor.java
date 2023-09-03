package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringTokenizer;
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

    private static void withNoURI(final OutputStream outputStream, final String httpRequestMethod,
                                  final String httpRequestUri) throws IOException {
        if (Objects.equals(httpRequestMethod, "GET") && Objects.equals(httpRequestUri, "/")) {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        }
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream();) {

            final StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
            final String httpRequestMethod = stringTokenizer.nextToken();
            final String httpRequestUri = stringTokenizer.nextToken();

            withNoURI(outputStream, httpRequestMethod, httpRequestUri);

            withIndexPage(outputStream, httpRequestMethod, httpRequestUri);


        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void withIndexPage(final OutputStream outputStream, final String httpRequestMethod,
                               final String httpRequestUri)
            throws IOException {
        if (Objects.equals(httpRequestMethod, "GET") && Objects.equals(httpRequestUri, "/index.html")) {
            final var responseBody = new StringBuilder();

            final URL indexPageURL = this.getClass().getClassLoader().getResource("static/index.html");
            final File indexFile;
            try {
                indexFile = new File(indexPageURL.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            try (
                    final FileInputStream fileInputStream = new FileInputStream(indexFile);
                    final BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(fileInputStream, StandardCharsets.UTF_8))
            ) {
                while (bufferedReader.ready()) {
                    responseBody
                            .append(bufferedReader.readLine())
                            .append(System.lineSeparator());
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.toString().getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        }
    }
}
