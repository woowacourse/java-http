package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<String> ALLOWED_PATHS = List.of(
        "/", "/index.html", "/index", "/login.html", "/login", "/401.html",
        "/assets/chart-area.js", "/assets/chart-bar.js", "/assets/chart-pie.js",
        "/css/styles.css",
        "/js/scripts.js");

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
        final String charSetOption = ";charset=utf-8";
        try (final var inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {
            RequestTarget requestTarget = getRequestTarget(bufferedReader);

            Response response = dispatchRequest(requestTarget);
            response.addBody(readStaticResource(response.filePath()));
            response.addHeader("Content-Type", getContentType(response.filePath()) + charSetOption);
            response.addHeader("Content-Length", String.valueOf(response.body()
                .getBytes().length));

            final String message = generateResponseMessage(response);

            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestTarget getRequestTarget(final BufferedReader bufferedReader) throws IOException {
        final String uri = bufferedReader.readLine()
            .split(" ")[1];
        final Map<String, String> target = parseTarget(uri);
        final String path = target.get("filePath");
        final Map<String, String> queryParams = extractQueryParams(target.get("queryString"));

        return new RequestTarget(path, queryParams);
    }

    private Map<String, String> parseTarget(String uri) {
        final String queryDelimiter = "?";
        String path = "";
        String queryString = "";
        if (!uri.contains(queryDelimiter)) {
            path = uri;
            queryString = "";
        }
        if (uri.contains(queryDelimiter)) {
            final int queryIndex = uri.indexOf(queryDelimiter);
            path = uri.substring(0, queryIndex);
            queryString = uri.substring(queryIndex + 1);
        }
        return Map.of(
            "filePath", path,
            "queryString", queryString);
    }

    private Map<String, String> extractQueryParams(final String queryString) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        if (queryString.isBlank()) {
            return queryParams;
        }
        Arrays.stream(queryString.split("&"))
            .map(keyValue -> keyValue.split("="))
            .forEach(split -> queryParams.put(split[0], split[1]));

        return queryParams;
    }

    private Response dispatchRequest(final RequestTarget requestTarget) {
        final List<String> indexPaths = List.of("/index", "/index.html");
        if (!ALLOWED_PATHS.contains(requestTarget.path())) {
            return new Response(HttpStatus.NOT_FOUND, "/404.html");
        }
        if (Objects.equals(requestTarget.path(), "/login")) {
            return handleLogin(requestTarget.queryParams());
        }
        if (indexPaths.contains(requestTarget.path())) {
            return new Response(HttpStatus.OK, "/index.html");
        }
        return new Response(HttpStatus.OK, requestTarget.path());
    }

    private Response handleLogin(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            return new Response(HttpStatus.OK, "/login.html");
        }
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow();
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            return Response.found("/index.html", "/index.html");
        }

        return Response.found("/401.html", "/401.html");
    }

    private String getContentType(final String filePath) {
        final String defaultContentType = "text/html";
        if (filePath.equals("/")) {
            return defaultContentType;
        }
        final String prefix = "text/";
        final int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + filePath.substring(lastDotIndex + 1);
    }

    private String readStaticResource(final String filePath) throws IOException {
        if (filePath.equals("/")) {
            return "Hello world!";
        }
        final String staticResourceTarget = "/static" + filePath;

        final URL resource = getClass().getResource(staticResourceTarget);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스가 존재하지 않습니다 (filePath: " + staticResourceTarget);
        }

        return new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
    }

    private String generateResponseMessage(final Response response)  {
        return String.join("\r\n",
            "HTTP/1.1 " + response.httpStatusCode() + " " + response.httpStatusName() + " ",
            response.headerString(),
            "",
            response.body());
    }
}
