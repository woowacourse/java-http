package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

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
import java.util.Collections;
import java.util.HashMap;
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
            if (requestLine == null) {
                throw new IllegalArgumentException("invalid http request");
            }

            // 요청 헤더 파싱
            String requestMethod = requestLine.split(" ")[0];
            String requestUri = requestLine.split(" ")[1];
            String requestUriPath = getRequestUriPath(requestUri);
            Map<String, String> queryParameters = getQueryParameters(requestUri);

            // 응답
            final HttpResponse response = getHttpResponse(requestMethod, requestUriPath, queryParameters);
            outputStream.write(response.toString().getBytes(UTF_8));
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
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(queryString.split("&"))
            .map(parameter -> parameter.split("="))
            .forEach(keyValue -> queryParameters.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
        return Collections.unmodifiableMap(queryParameters);
    }

    private HttpResponse getHttpResponse(
        String requestMethod,
        String requestUriPath,
        Map<String, String> queryParameters
    ) throws IOException {
        if (requestMethod.equals("GET") && requestUriPath.equals("/")) {
            String responseBody = "Hello world!";
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/html;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".css")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/css;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".html")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .body(responseBody)
                .contentType("text/html;charset=utf-8")
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.endsWith(".js")) {
            String responseBody = readStaticFile(requestUriPath);
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .contentType("text/javascript;charset=utf-8")
                .body(responseBody)
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.equals("/login") && queryParameters.isEmpty()) {
            String responseBody = readStaticFile("/login.html");
            return HttpResponse.builder()
                .status(HttpStatus.OK)
                .contentType("text/html;charset=utf-8")
                .body(responseBody)
                .build();
        }
        if (requestMethod.equals("GET") && requestUriPath.equals("/login")) {
            try {
                login(queryParameters);
            } catch (UnAuthorizedException e) {
                return HttpResponse.builder()
                    .status(HttpStatus.Found)
                    .header("Location", "/401.html")
                    .body("")
                    .build();
            }
            return HttpResponse.builder()
                .status(HttpStatus.Found)
                .header("Location", "/index.html")
                .body("")
                .build();
        }
        throw new IllegalArgumentException("invalid request");
    }

    private void login(Map<String, String> keyValues) {
        String account = keyValues.get("account");
        String password = keyValues.get("password");
        if (account != null && password != null) {
            Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
            boolean isValidAccount = findUser.isPresent();
            if (!isValidAccount) {
                throw new UnAuthorizedException("Invalid account " + account);
            }
            User user = findUser.get();
            log.atInfo().log("user: {}", user);
        }
        throw new UnAuthorizedException("account or password should be not null");
    }

    private String readStaticFile(String filePath) throws IOException {
        String staticFilePath = STATIC_RESOURCE_PATH + filePath;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        if (resource == null) {
            throw new IllegalArgumentException("리소스가 존재하지 않습니다. " + staticFilePath);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
