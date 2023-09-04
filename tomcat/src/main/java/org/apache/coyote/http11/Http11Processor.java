package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
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

            final List<String> request = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .takeWhile(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            final String requestLine = request.get(0);
            final String[] requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String requestUri = requestLineTokens[1];

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            if (method.equals("GET") && requestUri.contains(".")) {
                generateResourceResponse(requestUri, outputStream);
                return;
            }


                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            generateDefaultResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void generateDefaultResponse(final OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(ContentType.HTML)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void generateResourceResponse(final String requestUri, final OutputStream outputStream) throws IOException {
        final String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);
        final ContentType contentType = ContentType.of(extension);
        final var resource = getClass().getClassLoader().getResource("static/" + requestUri);

        final String responseBody = Files.readString(Path.of(Objects.requireNonNull(resource).getPath()));

        final var response = new HttpResponse(HttpStatus.OK)
                .addContentType(contentType)
                .addContentLength(responseBody.length())
                .build(responseBody);

        log.info("response: {}", response);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
