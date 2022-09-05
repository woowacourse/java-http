package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
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
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final var request = readRequestHeader(bufferedReader);
            final var response = getResponse(HttpRequest.from(request));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        StringBuilder header = new StringBuilder();

        while (bufferedReader.ready()) {
            header.append(bufferedReader.readLine())
                    .append("\r\n");
        }
        return header.toString();
    }

    private String getResponse(final HttpRequest httpRequest) throws IOException {
        return getResponse(httpRequest.getPath(), httpRequest.getRequestParams());
    }

    private String getResponse(String url, final Map<String, String> requestParam) throws IOException {
        if ("/".equals(url)) {
            return createResponse(ContentType.HTML, "Hello world!");
        }

        if ("/login".equals(url)) {
            User user = InMemoryUserRepository.findByAccount(requestParam.get("account"))
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
            if (user.checkPassword(requestParam.get("password"))) {
                log.info(user.toString());
            }

            return createResponse(ContentType.HTML, readFile(url + ".html"));
        }

        return createResponse(ContentType.from(url), readFile(url));
    }

    private String readFile(String url) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + url);
        return Files.readString(new File(Objects.requireNonNull(resource).getFile()).toPath());
    }

    private String createResponse(ContentType contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getContent() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "", responseBody);
    }
}
