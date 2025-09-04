package org.apache.coyote.http11;

import static java.util.stream.Collectors.toUnmodifiableMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            // 요청 헤더
            String requestLine = bufferedReader.readLine();

            // 요청 헤더 파싱
            String requestUri = requestLine.split(" ")[1];
            String requestUriPath = getRequestUriPath(requestUri);
            Map<String, String> queryParameters = getQueryParameters(requestUri);

            // 응답
            String responseBody = getResponseBody(requestUriPath, queryParameters);
            String contentType = getContentType(requestUriPath);
            final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: %s;charset=utf-8 ".formatted(contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestUriPath(String requestUri) {
        int index = requestUri.lastIndexOf("?");
        if (index == -1) {
            return requestUri;
        }
        return requestUri.substring(0, requestUri.lastIndexOf("?"));
    }

    private Map<String, String> getQueryParameters(String requestUri) {
        int index = requestUri.lastIndexOf("?");
        if (index == -1) {
            return Map.of();
        }
        String queryString = requestUri.substring(index + 1);
        return Arrays.stream(queryString.split("&"))
            .map(parameter -> parameter.split("="))
            .collect(toUnmodifiableMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }

    private String getResponseBody(String requestUriPath, Map<String, String> keyValues) throws IOException {
        if (requestUriPath.equals("/")) {
            return "Hello world!";
        }
        if (requestUriPath.equals("/login")) {
            login(keyValues);
            return readStaticFile("/login.html");
        }
        return readStaticFile(requestUriPath);
    }

    private void login(Map<String, String> keyValues) {
        String account = keyValues.get("account");
        String password = keyValues.get("password");
        if (account != null && password != null) {
            Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
            boolean isValidAccount = findUser.isPresent();
            if (!isValidAccount) {
                throw new IllegalArgumentException("Invalid account " + account);
            }
            User user = findUser.get();
            log.atInfo().log("user: {}", user);
        }
    }

    private String readStaticFile(String filePath) throws IOException {
        String staticFilePath = STATIC_RESOURCE_PATH + filePath;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getContentType(String requestUriPath) throws IOException {
        if (requestUriPath.equals("/")) {
            return "text/html";
        }
        if (requestUriPath.equals("/login")) {
            return "text/html";
        }
        if (requestUriPath.endsWith(".css")) {
            return "text/css";
        }
        if (requestUriPath.endsWith(".html")) {
            return "text/html";
        }
        if (requestUriPath.endsWith(".js")) {
            return "text/javascript";
        }
        throw new IllegalArgumentException("지원하지 않는 요청 uri 입니다. " + requestUriPath);
    }
}
