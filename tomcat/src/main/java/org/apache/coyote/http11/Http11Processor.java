package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_ROOT = "static/";
    private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE_CSS = "text/css;charset=utf-8";

    private static final String INDEX_PATH = "/index.html";
    private static final String CSS_PATH = "/css/styles.css";
    private static final String LOGIN_PATH = "/login";

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String requestLine = bufferedReader.readLine();

            if (requestLine == null) {
                return;
            }

            String uri = requestLine.split(" ")[1];

            String path = uri;
            String queryString = "";

            int index = uri.indexOf("?");

            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            String line = bufferedReader.readLine();

            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }

                line = bufferedReader.readLine();
            }

            var responseBody = "Hello world!";
            var contentType = CONTENT_TYPE_HTML;

            if (path.equals(INDEX_PATH)) {
                responseBody = readStaticFile(path.substring(1));
            }

            if (path.equals(CSS_PATH)) {
                responseBody = readStaticFile(path.substring(1));
                contentType = CONTENT_TYPE_CSS;
            }

            if (path.equals(LOGIN_PATH)) {
                responseBody = readStaticFile("login.html");

                if (!queryString.isEmpty()) {
                    Map<String, String> parameters = parseQueryString(queryString);

                    String account = parameters.getOrDefault("account", "");
                    String password = parameters.getOrDefault("password", "");

                    var user = InMemoryUserRepository.findByAccount(account);

                    if (user.isPresent()) {
                        if (user.get().checkPassword(password)) {
                            log.info("로그인 성공 : account={}", user.get().getAccount());
                        }
                    }
                }
            }

            final var response = createResponse(contentType, responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            parameters.put(keyValue[0], keyValue[1]);
        }

        return parameters;
    }

    private String readStaticFile(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader()
                .getResource(STATIC_RESOURCE_ROOT + fileName);

        Path filePath = Path.of(resource.toURI());
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }
}
