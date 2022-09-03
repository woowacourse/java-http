package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_REQUEST_BODY = "Hello world!";

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
        try (final BufferedReader bufferedReader
                     = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            List<String> request = readRequest(bufferedReader);
            HttpRequest httpRequest = HttpRequest.of(request);

            final String responseBody = getResponseBody(httpRequest.extractRequestUri());
            final String response = makeResponse(httpRequest.findContentType(), responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private String getResponseBody(final RequestUri requestUri) throws IOException {
        if (requestUri.hasQueryParams()) {
            final User user = getUserByAccount(requestUri.findQueryParamValue("account"));
            validateUserPassword(requestUri, user);
            log.info("user : " + user);
        }
        if (requestUri.isResourceFileRequest()) {
            return readResourceFile(requestUri.getResourcePath());
        }
        return DEFAULT_REQUEST_BODY;
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchUserException("존재하지 않는 회원입니다."));
    }

    private static void validateUserPassword(final RequestUri requestUri, final User user) {
        if (!user.checkPassword(requestUri.findQueryParamValue("password"))) {
            throw new NoSuchUserException("비밀번호가 일치하지 않습니다.");
        }
    }

    private String readResourceFile(final String resourcePath) throws IOException {
        final URL url = getClass().getClassLoader().getResource(resourcePath);
        final Path path = new File(url.getFile()).toPath();
        return new String(Files.readAllBytes(path));
    }

    private String makeResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
