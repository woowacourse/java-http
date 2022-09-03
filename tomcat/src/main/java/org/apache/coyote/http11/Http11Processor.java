package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE_URL = "/login.html";
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final User EMPTY_USER = new User("", "", "");

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String uri = getUri(bufferedReader);
            final String path = getPath(uri);
            final Parameters queryParameters = Parameters.fromUri(uri);
            final String responseBody = getResponseBody(path, queryParameters);
            final String contentType = getContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine()
                .split(" ")[1];
    }

    private String getPath(final String path) {
        return path.split("\\?")[0];
    }

    private String getResponseBody(final String path, final Parameters parameters) throws IOException {
        final String url = rewrite(path);
        if (url.equals("/")) {
            return "Hello world!";
        }
        if (url.equals(LOGIN_PAGE_URL)) {
            loggingAccount(parameters);
        }

        final String filePath = getClass().getClassLoader().getResource("static" + url).getPath();
        final FileInputStream fileInputStream = new FileInputStream(filePath);

        final String responseBody = new String(fileInputStream.readAllBytes());
        fileInputStream.close();

        return responseBody;
    }

    private String rewrite(final String path) {
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE_URL;
        }
        return path;
    }

    private void loggingAccount(final Parameters parameters) {
        final User user = InMemoryUserRepository.findByAccount(parameters.get(ACCOUNT))
                .orElse(EMPTY_USER);
        final String password = parameters.get(PASSWORD);

        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String getContentType(final String path) throws IOException {
        final String contentType = Files.probeContentType(Path.of(path));
        if (contentType == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        return contentType;
    }
}
