package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final int REQUEST_TARGET_INDEX = 1;
    public static final String STATIC = "static";
    private static final String CSS_EXTENSION = ".css";
    public static final String HTTP_1_1_200_OK = "HTTP/1.1 200 OK ";
    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html;charset=utf-8 ";
    public static final String CONTENT_TYPE_CSS = "Content-Type: text/css;charset=utf-8 ";

    public static final String CONTENT_LENGTH = "Content-Length: ";
    public static final String HELLO_WORLD = "Hello world!";
    public static final String HOME_PATH = "/";
    public static final String CRLF = "\r\n";

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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();

            String line = bufferedReader.readLine();
            while (line != null && !line.isBlank()) {
                stringBuilder.append(line).append(CRLF);
                line = bufferedReader.readLine();
            }

            String requestUri = stringBuilder.toString().split(" ")[REQUEST_TARGET_INDEX];

            Map<String, String> queryParameters = new HashMap<>();
            if (requestUri.contains("?")) {
                String[] uriParts = requestUri.split("\\?", 2);
                requestUri = uriParts[0];

                String queryString = uriParts[1];
                String[] queryPairs = queryString.split("&");
                for (String queryPair : queryPairs) {
                    String[] keyAndValue = queryPair.split("=");

                    queryParameters.put(keyAndValue[0], keyAndValue[1]);
                }

                if(requestUri.contains("/login")){
                    Optional<User> user = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
                    if (user.isEmpty()) {
                        throw new NoSuchElementException("존재하지 않는 사용자입니다.");
                    }

                    User foundUser = user.get();
                    if (foundUser.checkPassword(queryParameters.get("password"))) {
                        log.info("user : {}", foundUser);
                    }
                }
            }

            final String responseBody = getResponseBody(requestUri);
            if (responseBody == null) {
                return;
            }
            String contentType = getContentType(requestUri);

            final var response = String.join(CRLF,
                    HTTP_1_1_200_OK,
                    contentType,
                    CONTENT_LENGTH + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(CSS_EXTENSION)) {
            return CONTENT_TYPE_CSS;
        }
        return CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8;
    }

    private String getResponseBody(String requestUri) throws URISyntaxException, IOException {
        if (Objects.equals(requestUri, HOME_PATH)) {
            return HELLO_WORLD;
        }

        if (requestUri.contains("/login")) {
            requestUri = "/login.html";
        }

        final URL resource = getClass().getClassLoader().getResource(STATIC + requestUri);
        if (resource == null) {
            log.warn("존재하지 않는 경로 : {}", requestUri);
            return null;
        }
        final Path path = Paths.get(resource.toURI());

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }
}
