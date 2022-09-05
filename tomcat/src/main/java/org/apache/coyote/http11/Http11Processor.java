package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
    private static final User EMPTY_USER = new User(null, null, null);

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
            final String path = rewrite(getPath(uri));
            final Parameters queryParameters = Parameters.fromUri(uri);

            writeLogging(path, queryParameters);

            final String response = getResponse(path);

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

    private String rewrite(final String path) {
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE_URL;
        }
        return path;
    }

    private String getResponse(final String path) throws IOException {
        final String contentType = getContentType(path);
        final String responseBody = getResponseBody(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        return readFile(path);
    }

    private void writeLogging(final String path, final Parameters parameters) {
        if (path.equals(LOGIN_PAGE_URL)) {
            final User user = getUser(parameters.get(ACCOUNT));
            loggingAccount(user, parameters);
        }
    }

    private User getUser(final String account) {
        if (account == null || account.isBlank()) {
            return EMPTY_USER;
        }
        try {
            return InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException(account + "가 없습니다."));
        } catch (final IllegalArgumentException e) {
            log.info(e.getMessage());
            return EMPTY_USER;
        }
    }

    private void loggingAccount(final User user, final Parameters parameters) {
        final String password = parameters.get(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String readFile(final String url) throws IOException {
        final URL fileUrl = getClass().getClassLoader().getResource("static" + url);
        if (fileUrl == null) {
            throw new NoSuchFileException(url + " 파일이 없습니다.");
        }

        try (final FileInputStream fileInputStream = new FileInputStream(fileUrl.getPath())) {
            return new String(fileInputStream.readAllBytes());
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
