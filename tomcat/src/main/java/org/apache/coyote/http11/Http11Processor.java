package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.InMemoryUserRepository;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SAFARI_CHROME_ACCEPT_HEADER_DEFAULT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";

    private final Socket connection;
    private final InMemoryUserRepository userRepository;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.userRepository = InMemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            List<String> headers = new ArrayList<>();

            String header = "";
            while (!(header = bufferedReader.readLine()).equals("")) {
                headers.add(header);
            }
            String[] splitFirstLine = Objects.requireNonNull(headers.get(0)).split(" ");
            String requestMethod = splitFirstLine[0];
            String requestUri = splitFirstLine[1];

            String requestAcceptHeader = findAcceptHeader(headers);
            String contentTypeHeader = getContentTypeHeaderFrom(requestAcceptHeader);

            RequestHandler requestHandler = getFilePathAndStatus(requestMethod, requestUri);
            String responseBody = readFile(requestHandler.getResponseFilePath());

            List<String> responseHeaders = new ArrayList<>();
            responseHeaders.add("HTTP/1.1 " + requestHandler.getHttpStatus() + " ");
            responseHeaders.add(contentTypeHeader);
            responseHeaders.add("Content-Length: " + responseBody.getBytes().length + " ");
            String responseHeader = String.join("\r\n", responseHeaders);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            var response = String.join("\r\n", responseHeader, "", responseBody);

            bufferedOutputStream.write(response.getBytes());
            bufferedOutputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String findAcceptHeader(List<String> headers) {
        return headers.stream()
                      .filter(it -> it.startsWith("Accept"))
                      .findFirst()
                      .orElse("Accept: " + SAFARI_CHROME_ACCEPT_HEADER_DEFAULT_VALUE);
    }

    private static String getContentTypeHeaderFrom(String requestAcceptHeader) {
        String[] splitAcceptHeader = requestAcceptHeader.split(" ");
        String headerValue = splitAcceptHeader[1];
        String[] acceptTypes = headerValue.split(";");
        String[] splitAcceptTypes = acceptTypes[0].split(",");

        if (Arrays.asList(splitAcceptTypes).contains("text/css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private RequestHandler getFilePathAndStatus(String requestMethod, String requestUri) {
        if (!requestMethod.equalsIgnoreCase("GET")) {
            throw new IllegalArgumentException("GET 요청만 처리 가능합니다.");
        }

        int index = requestUri.indexOf("?");
        String requestPath = requestUri;

        if (index != -1) {
            requestPath = requestUri.substring(0, index);
            String queryString = requestUri.substring(index + 1);

            String[] splitQueryString = queryString.split("&");

            if (isLoginRequest(requestMethod, requestPath)) {
                return handleLoginRequest(splitQueryString);
            }
        }

        String fileName = "static" + requestPath;
        return RequestHandler.of("200 OK", fileName);
    }

    private static boolean isLoginRequest(String requestMethod, String requestPath) {
        boolean isLoginUri = requestPath.equals("/login.html") || requestPath.equals("/login");
        return requestMethod.equalsIgnoreCase("GET") && isLoginUri;
    }

    private RequestHandler handleLoginRequest(String[] splitQueryString) {
        Optional<String> account = getValueOf("account", splitQueryString);
        Optional<String> password = getValueOf("password", splitQueryString);

        if (account.isEmpty() && password.isEmpty()) {
            return RequestHandler.of("200 OK", "static/login.html");
        }

        Optional<User> user = findUserByAccount(account.get());
        if (user.isPresent() && user.get().checkPassword(password.get())) {
            log.info(user.get().toString());
            return RequestHandler.of("302 Found", "static/index.html");
        }
        return RequestHandler.of("401 Unauthorized", "static/401.html");
    }

    private String readFile(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return "Hello world!";
        }
    }

    private Optional<String> getValueOf(String key, String[] splitQueryString) {
        return Arrays.stream(splitQueryString)
                     .filter(it -> equalsKey(key, it))
                     .map(it -> it.substring(it.indexOf("=") + 1))
                     .findFirst();
    }

    private boolean equalsKey(String expected, String actual) {
        String[] splitActual = actual.split("=");
        return splitActual[0].equals(expected);
    }

    private Optional<User> findUserByAccount(String account) {
        List<User> users = userRepository.findAll();
        return users.stream()
                    .filter(it -> it.equalsAccount(account))
                    .findFirst();
    }
}
