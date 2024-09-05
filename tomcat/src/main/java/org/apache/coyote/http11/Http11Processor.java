package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_DIRECTORY_NAME = "static";
    private static final String DEFAULT_PAGE_URI = DEFAULT_DIRECTORY_NAME + "/index.html";


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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            RequestLine requestLine = new RequestLine(bufferedReader.readLine());

            if (isLogin(requestLine)) {
                checkLogin(requestLine);
            }

            Map<String, String> requestHeaders = makeRequestHeaders(bufferedReader);
            String body = makeResponseBody(requestLine.getRequestURI());

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + findResponseContentType(requestLine.getRequestURI()) + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> makeRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }
            String[] split = line.split(":");
            String key = split[0].trim();
            String value = split[1].trim();
            requestHeaders.put(key, value);
        }
        return requestHeaders;
    }

    private String makeResponseBody(String requestUrl) throws IOException {
        final Path path = findPath(requestUrl);
        return Files.readString(path);
    }

    private Path findPath(String url) {
        try {
            if (url.equals("/")) {
                URL url1 = getClass().getClassLoader().getResource(DEFAULT_PAGE_URI);
                return Path.of(Objects.requireNonNull(url1).toURI());
            }

            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?")) + ".html";
            }

            URL foundUrl = getClass().getClassLoader().getResource(DEFAULT_DIRECTORY_NAME + url);
            return Path.of(Objects.requireNonNull(foundUrl).toURI());
        } catch (URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String findResponseContentType(String url) {
        Path path = findPath(url);
        return "text/" + path.toString().split("\\.")[1];
    }

    private boolean isLogin(RequestLine requestLine) {
        String uri = requestLine.getRequestURI();
        return uri.contains("login");
    }

    private void checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException(account + " " + password + "잘못된 유저 요청입니다.");
        }

        log.info("user : {}", user);
    }
}
