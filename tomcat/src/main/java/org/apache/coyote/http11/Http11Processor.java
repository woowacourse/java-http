package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FILE_LOCATION = "/Users/ichaeyeong/Desktop/woowacourse/level3/java-http/tomcat/src/main/resources/static";

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
            final Http11Request request = findRequest(inputStream);

            final Http11Response response = findResponse(request);
            final String responseMessage = response.toString();

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (final IllegalArgumentException e) {
            log.warn(String.format("bad request : %s", e.getMessage()));
        } catch (final NoSuchFileException e) {
            log.warn(String.format("file not found : %s", e.getMessage()));
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                connection.close();
            } catch (final IOException e) {
                log.error("failed to close connection", e);
            }
        }
    }

    private Http11Response findResponse(final Http11Request request) throws IOException {
        final String requestTarget = request.getTarget();

        if (requestTarget.equals("/")) {
            final byte[] defaultResponseBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);

            return createHtmlResponse(defaultResponseBytes);
        }
        if (requestTarget.equals("/index.html")) {
            final byte[] fileContent = readFile(requestTarget);

            return createHtmlResponse(fileContent);
        }
        if (requestTarget.contains("/login")) {
            final byte[] fileContent = readFile("login.html");

            final String account = request.findQueryParam("account");
            final String password = request.findQueryParam("password");

            validateUserByAccount(account, password);

            return createHtmlResponse(fileContent);
        }
        final byte[] fileContent = readFile(requestTarget);

        return createCssResponse(fileContent);
    }

    private Http11Request findRequest(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final List<String> requestMessage = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            requestMessage.add(line);
        }

        return Http11Request.create(requestMessage);
    }

    private byte[] readFile(final String location) throws IOException {
        final var path = Path.of(STATIC_FILE_LOCATION, location);
        return Files.readAllBytes(path);
    }

    private void validateUserByAccount(final String account, final String password) {
        final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);

        if (userOrEmpty.isEmpty()) {
            log.warn(String.format("User not found : account = %s", account));
            return;
        }

        final User user = userOrEmpty.get();
        if (!user.checkPassword(password)) {
            log.warn(String.format("Wrong password : account = %s", account));
            return;
        }

        log.info(String.format("User found : %s", user));
    }

    private static Http11Response createHtmlResponse(final byte[] body) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));

        return new Http11Response(
                "HTTP/1.1",
                200,
                "OK",
                headers,
                new String(body, StandardCharsets.UTF_8)
        );
    }

    private static Http11Response createCssResponse(final byte[] body) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/css;charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));

        return new Http11Response(
                "HTTP/1.1",
                200,
                "OK",
                headers,
                new String(body, StandardCharsets.UTF_8)
        );
    }
}
