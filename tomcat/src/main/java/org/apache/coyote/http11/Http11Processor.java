package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var uri = parseUri(bufferedReader);
            int index = uri.indexOf("?");
            final var path = getPath(uri);

            if (path.equals("/login")) {
                final var queryString = uri.substring(index + 1).split("&");
                final var account = queryString[0].substring(queryString[0].lastIndexOf("=") + 1);
                final var password = queryString[1].substring(queryString[1].lastIndexOf("=") + 1);

                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow();

                if (user.checkPassword(password)) {
                    log.info("{}", user);
                }
            }

            final var contentType = parseContentType(path);
            final var responseBody = createResponseBody(path);

            final var response = createResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(final String uri) {
        final int index = uri.indexOf("?");

        if (index != -1) {
            return uri.substring(0, index);
        }
        return uri;
    }

    private String parseUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine().split(" ")[1];
    }

    private ContentType parseContentType(final String uri) {
        final var extension = uri.substring(uri.lastIndexOf(".") + 1);
        return ContentType.of(extension);
    }

    private String createResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        } else if (!uri.contains(".")) {
            uri = uri + ".html";
        }

        URL resource = getClass().getClassLoader().getResource("static/" + uri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String createResponse(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
