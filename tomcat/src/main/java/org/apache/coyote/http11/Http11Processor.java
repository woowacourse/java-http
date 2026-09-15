package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

            RequestTarget requestTarget = getRequestTarget(inputStream);
            String requestPath = requestTarget.path();

            if ("/login".equals(requestPath)) {
                logLoginUser(requestTarget.queryParams());
            }

            String responseBody = getResponseBody(requestPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(requestPath) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        File file = getPageFile(requestPath);
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    private RequestTarget getRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        String[] requestParts = requestLine.split(" ", 3);
        if (requestParts.length != 3) {
            throw new IOException("잘못된 HTTP 요청 라인입니다: " + requestLine);
        }

        String[] targetParts = requestParts[1].split("\\?", 2);

        return RequestTarget.of(targetParts);
    }

    private void logLoginUser(Map<String, String> queryParams) {

        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("login user: {}", user));
    }

    private File getPageFile(String path) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("static" + path);

        if (resource == null) {
            resource = classLoader.getResource("static" + path + ".html");
        }

        if (resource == null) {
            throw new FileNotFoundException(path);
        }

        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html";
    }

    private record RequestTarget(String path, Map<String, String> queryParams) {
        public static RequestTarget of(String[] target) {
            if (target.length == 1) {
                return new RequestTarget(target[0], Map.of());
            }

            Map<String, String> queryParams = Arrays.stream(target[1].split("&"))
                    .map(parameter -> parameter.split("=", 2))
                    .filter(parameter -> parameter.length == 2)
                    .collect(Collectors.toUnmodifiableMap(
                            parameter -> decode(parameter[0]),
                            parameter -> decode(parameter[1]),
                            (previous, replacement) -> replacement
                    ));

            return new RequestTarget(target[0], queryParams);
        }

        private static String decode(String value) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
    }

}
