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
import java.util.UUID;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.RequestLine;
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
    private static final String REGISTER_PAGE_URL = "/register.html";
    private static final String REGISTER_PATH = "/register";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final User EMPTY_USER = new User(null, null, null);
    private static final String POST = "POST";
    private static final String GET = "GET";

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

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            final RequestLine requestLine = httpRequest.getRequestLine();
            final String uri = requestLine.getUri();
            final String path = rewrite(getPath(uri), requestLine.getMethod());

            final String response = handle(path, httpRequest);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handle(final String path, final HttpRequest httpRequest)
            throws IOException {
        final RequestLine requestLine = httpRequest.getRequestLine();
        final String httpVersion = requestLine.getVersion();
        if (requestLine.getMethod().equals(POST)) {
            return post(path, httpRequest, httpVersion);
        }
        return get(path, httpRequest, httpVersion);
    }

    private String post(final String path, final HttpRequest httpRequest, final String httpVersion) throws IOException {
        StatusLine statusLine = new StatusLine(httpVersion, 200, "OK");
        final Map<String, String> headers = new HashMap<>();
        final String responseBody = getResponseBody(path);

        headers.put("contentType", getContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        if (path.equals(LOGIN_PAGE_URL)) {
            final Parameters loginParameters = Parameters.parseParameters(httpRequest.getRequestBody());
            final User user = getUser(loginParameters.get(ACCOUNT));
            statusLine = new StatusLine(httpVersion, 302, "Found");
            headers.put("Location", "/401.html");

            if (user.checkPassword(loginParameters.get(PASSWORD))) {
                log.info(user.toString());
                headers.put("Location", "/index.html");
                final UUID uuid = UUID.randomUUID();
                headers.put("Set-Cookie", "JSESSIONID=" + uuid);
            }
            return getResponse(path, statusLine, headers);
        }
        if (path.equals(REGISTER_PAGE_URL)) {
            final Parameters loginParameters = Parameters.parseParameters(httpRequest.getRequestBody());
            final String account = loginParameters.get(ACCOUNT);
            final String password = loginParameters.get(PASSWORD);
            final String email = loginParameters.get(EMAIL);
            final User user = new User(account, password, email);

            InMemoryUserRepository.save(user);
            statusLine = new StatusLine(httpVersion, 302, "Found");
            headers.put("Location", "/index.html");
            return getResponse(path, statusLine, headers);
        }

        return getResponse(path, statusLine, headers);
    }

    private String get(final String path, final HttpRequest httpRequest, final String httpVersion) throws IOException {
        final StatusLine statusLine = new StatusLine(httpVersion, 200, "OK");
        final Map<String, String> headers = new HashMap<>();
        final String responseBody = getResponseBody(path);

        headers.put("contentType", getContentType(path) + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        return getResponse(path, statusLine, headers);
    }

    private String getPath(final String path) {
        return path.split("\\?")[0];
    }

    private String rewrite(final String path, final String method) {
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE_URL;
        }
        if (path.equals(REGISTER_PATH)) {
            return REGISTER_PAGE_URL;
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
