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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.LoginResult;
import nextstep.jwp.model.StatusLine;
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
    private static final String HTTP_VERSION = "HTTP/1.1";

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

            final String response = handle(path, queryParameters);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handle(final String path, final Parameters parameters) throws IOException {
        StatusLine statusLine = new StatusLine(HTTP_VERSION, "200", "OK");
        final Map<String, String> headers = new HashMap<>();

        if (path.equals(LOGIN_PAGE_URL)) {
            final LoginResult loginResult = LoginResult
                    .from(getUser(parameters.get(ACCOUNT)), parameters.get(PASSWORD));
            headers.put("Location", loginResult.getLocation());
            statusLine = new StatusLine(HTTP_VERSION, loginResult.getStatusCode(), loginResult.getReasonPhrase());
        }

        final String responseBody = getResponseBody(path);
        headers.put("contentType", getContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        return getResponse(path, statusLine, headers);
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

    private String getResponse(final String path, final StatusLine statusLine, final Map<String, String> headers)
            throws IOException {
        final String responseBody = getResponseBody(path);
        headers.put("contentType", getContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        final String header = headers.keySet().stream()
                .map(it -> it + ": " + headers.get(it))
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                statusLine.getResponse() + " ",
                header,
                "",
                responseBody);
    }

    private String getResponseBody(final String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        return readFile(path);
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
