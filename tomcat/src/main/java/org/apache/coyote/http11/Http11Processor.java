package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            String requestLine = extractRequestLine(inputStream);
            log.info("request line: {}", requestLine);
            if (requestLine == null) {
                outputStream.flush();
                return;
            }
            String endpoint = requestLine.split(" ")[1];

            Map<String, String> queryParams = extractQueryParams(endpoint);

            execute(endpoint, queryParams);
            String response = buildResponse(endpoint);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestLine(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

    private Map<String, String> extractQueryParams(String endpoint) {
        Map<String, String> queryParams = new HashMap<>();
        if (!endpoint.contains("?")) {
            return queryParams;
        }

        String queryString = endpoint.split("\\?")[1];
        String[] rawQueryParams = queryString.split("&");
        for (String rawQueryParam : rawQueryParams) {
            String key = rawQueryParam.split("=")[0];
            String value = rawQueryParam.split("=")[1];
            queryParams.put(key, value);
        }
        return queryParams;
    }

    private void execute(String endpoint, Map<String, String> queryParams) {
        if (endpoint.startsWith("/login")) {
            String userAccount = queryParams.get("account");
            String userPassword = queryParams.get("password");
            Optional<User> rawUser = InMemoryUserRepository.findByAccount(userAccount);
            rawUser.ifPresent(user -> {
                if (user.checkPassword(userPassword)) {
                    log.info("user: {}", user);
                }
            });
        }
    }

    private String buildResponse(String endpoint) throws IOException, URISyntaxException {
        String responseBody = decideResponseBody(endpoint);
        String fileExtension = extractFileExtension(endpoint);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + fileExtension + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String decideResponseBody(String endpoint) throws IOException, URISyntaxException {
        // TODO: refactoring
        if (endpoint.equals("/")) {
            return "Hello world!";
        }
        if (endpoint.startsWith("/login")) {
            URL resource = getClass().getResource("/static/login.html");
            return Files.readString(Paths.get(resource.toURI()));
        }
        URL resource = getClass().getResource("/static" + endpoint);
        if (resource == null) { // resources 하위에 존재하지 않는 경로라면
            return "Hello world!";
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

    private String extractFileExtension(String endpoint) {
        if (endpoint.contains(".")) {
            return endpoint.split("\\.")[1];
        }
        return "html";
    }
}
