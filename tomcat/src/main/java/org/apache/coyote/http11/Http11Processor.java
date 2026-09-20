package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_DIRECTORY = "static";

    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PATH = "index";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String REGISTER_PATH = "/register";
    private static final String REGISTER_PAGE = "/register.html";

    private static final String STATUS_OK = "200 OK";
    private static final String STATUS_FOUND = "302 Found";
    private static final String STATUS_BAD_REQUEST = "400 Bad Request";
    private static final String STATUS_UNAUTHORIZED = "401 Unauthorized";
    private static final String STATUS_NOT_FOUND = "404 Not Found";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

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
        try (final var reader = new BufferedReader(
                 new InputStreamReader(
                         connection.getInputStream(),
                         StandardCharsets.UTF_8
                 ));
             final var outputStream = connection.getOutputStream()) {

            handle(reader, outputStream);
        } catch (IOException | UncheckedServletException |URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final BufferedReader reader, final OutputStream outputStream)
            throws IOException, URISyntaxException {
        final String rawRequestLine = reader.readLine();
        if (rawRequestLine == null) {
            return;
        }

        try {
            final RequestHeaders headers = RequestHeaders.from(readHeaders(reader));
            final RequestBody body = RequestBody.from(readBody(reader, headers.getContentLength()));

            log.info("request: {}", rawRequestLine);
            final RequestLine requestLine = RequestLine.from(rawRequestLine);
            route(outputStream, requestLine, body);
        } catch (InvalidRequestException e) {
            log.warn("bad request: {}", e.getMessage());
            writeResponse(outputStream, STATUS_BAD_REQUEST, ContentType.HTML,
                    "400 Bad Request".getBytes(StandardCharsets.UTF_8));
        }
    }

    private void route(
            final OutputStream outputStream,
            final RequestLine requestLine,
            final RequestBody requestBody
    )
            throws IOException, URISyntaxException {
        final String path = requestLine.getPath();

        if (ROOT_PATH.equals(path)) {
            writeResponse(outputStream, STATUS_OK, ContentType.HTML,
                    "Hello world!".getBytes(StandardCharsets.UTF_8));
            return;
        }

        if (LOGIN_PATH.equals(path)) {
            if (POST.equals(requestLine.getMethod())) {
                String location = logLoginUser(requestBody);
                writeRedirect(outputStream, location);
                return;
            }

            writeStaticFile(outputStream, LOGIN_PAGE);
            return;
        }

        if (REGISTER_PATH.equals(path)) {
            if (POST.equals(requestLine.getMethod())) {
                String location = registerUser(requestBody);
                writeRedirect(outputStream, location);
                return;
            }
            writeStaticFile(outputStream, REGISTER_PAGE);
            return;
        }

        writeStaticFile(outputStream, path);
    }

    private String registerUser(RequestBody body) {
        final Optional<String> account = body.getParameter(ACCOUNT);
        final Optional<String> password = body.getParameter(PASSWORD);
        final Optional<String> email = body.getParameter(EMAIL);
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            log.info("회원 가입을 하기위해서는 셋 다 입력이 되어야 합니다.");
            return REGISTER_PAGE;
        }
        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));

        return INDEX_PAGE;
    }

    private List<String> readHeaders(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        List<String> lists = new ArrayList<>();
        while (line != null && !line.isEmpty()) {
            lists.add(line);
            line = reader.readLine();
        }

        return lists;
    }

    private String readBody(
            final BufferedReader reader,
            final int contentLength
            ) throws IOException {
        if (contentLength == 0) {
            return "";
        }
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }
        return new String(buffer, 0, totalRead);
    }

    private String logLoginUser(final RequestBody body) {
        final Optional<String> account = body.getParameter(ACCOUNT);
        final Optional<String> password = body.getParameter(PASSWORD);
        if (account.isEmpty() || password.isEmpty()) {
            log.info("login parameters are missing");
            return UNAUTHORIZED_PAGE;
        }
        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()))
                .map(user -> {
                    log.info("user: {}", user);
                    return INDEX_PAGE;
                })
                .orElseGet(() -> {
                    log.info("login failed. account: {}", account.get());
                    return UNAUTHORIZED_PAGE;
                });
    }

    private void writeStaticFile(final OutputStream outputStream, final String filePath)
            throws IOException, URISyntaxException {
        final Optional<Path> staticFile = findStaticFile(filePath);
        if (staticFile.isEmpty()) {
            writeResponse(outputStream, STATUS_NOT_FOUND, ContentType.HTML, readNotFoundBody());
            return;
        }
        writeResponse(outputStream, STATUS_OK, ContentType.from(filePath),
                Files.readAllBytes(staticFile.get()));
    }

    private Optional<Path> findStaticFile(final String url) throws URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + url);
        if (resource == null) {
            return Optional.empty();
        }

        final Path path = Path.of(resource.toURI());
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    private byte[] readNotFoundBody() throws IOException, URISyntaxException {
        final Optional<Path> notFoundPage = findStaticFile(NOT_FOUND_PAGE);
        if (notFoundPage.isPresent()) {
            return Files.readAllBytes(notFoundPage.get());
        }
        return "Not Found".getBytes(StandardCharsets.UTF_8);
    }

    private void writeRedirect(
            final OutputStream outputStream,
            final String location
    ) throws IOException {
        log.info("location: {}", location);

        final String header = String.join("\r\n",
                "HTTP/1.1 " + STATUS_FOUND + " ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final ContentType contentType,
            final byte[] body
    ) throws IOException {
        log.info("content-type: {}", contentType.getValue());

        final String header = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType.getValue() + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
