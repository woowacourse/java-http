package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".css", ".html", ".js");
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8 ";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private String makeResponse(String responseBody, Map<String, String> requests, HttpStatus httpStatus) {

        final String contentType = parseContentType(requests.getOrDefault("Accept", ""));
        final String protocol = requests.getOrDefault("Protocol", "");

        final String statusLine = protocol + " " + httpStatus.getCode() + " " + httpStatus.getCodeName() + " ";

        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
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
            String responseBody = "Hello world!";
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Map<String, String> requests = parseRequest(bufferedReader);
            HttpStatus httpStatus = HttpStatus.OK;

            Map<String, String> queries;

            final String path = requests.get("Path");

            queries = parseQueries(path);

            // ------------------- 컨트롤러 로직 ---------------------------
            if (!queries.isEmpty()) {
                String account = queries.get("account");
                String password = queries.get("password");

                try {
                    User user = InMemoryUserRepository.findByAccount(account)
                            .orElseThrow(IllegalArgumentException::new);

                    if (!user.checkPassword(password)) {
                        httpStatus = HttpStatus.UNAUTHORIZED;
                    }

                    if (user.checkPassword(password)) {
                        log.info("user: {}", user);
                        httpStatus = HttpStatus.FOUND;
                    }

                } catch (Exception e) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                }
            }

            if (!path.equals("/")) {
                responseBody = makeResponseBody(path, httpStatus);
            }

            // ------------------- 컨트롤러 로직 ---------------------------

            final var response = makeResponse(responseBody, requests, httpStatus);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseRequest(BufferedReader reader) throws IOException {
        Map<String, String> request = new HashMap<>();

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("유효하지 않은 요청입니다.");
        }
        parseRequestLine(requestLine, request);

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            parseHeader(headerLine, request);
        }

        return request;
    }

    private void parseRequestLine(String requestLine, Map<String, String> request) {
        String[] parts = requestLine.split(" ");
        if (parts.length >= 2) {
            request.put("Method", parts[0]);
            request.put("Path", parts[1]);
            if (parts.length >= 3) {
                request.put("Protocol", parts[2]);
            }
        }
    }

    private void parseHeader(String headerLine, Map<String, String> request) {
        final int separatorIndex = headerLine.indexOf(":");
        if (separatorIndex != -1) {
            String key = headerLine.substring(0, separatorIndex).trim();
            String value = headerLine.substring(separatorIndex + 1).trim();
            request.put(key, value);
        }
    }

    private String makeResponseBody(String resource, HttpStatus httpStatus) throws URISyntaxException, IOException {
        String filePath = "";

        if (httpStatus == HttpStatus.UNAUTHORIZED) {
            filePath = "/401.html";
        }

        if (httpStatus == HttpStatus.FOUND) {
            filePath = "/index.html";
        }

        if (httpStatus == HttpStatus.OK) {
            filePath = parseFilePath(resource);
        }

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL url = classLoader.getResource("static" + filePath);
        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }

        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        return new String(Files.readAllBytes(path));
    }

    private Map<String, String> parseQueries(String resource) {
        if (resource.contains("?")) {
            int questionIndex = resource.indexOf("?");

            String queryString = resource.substring(questionIndex + 1);
            String[] queryStrings = queryString.split("&");
            Map<String, String> queryKeyAndValues = new HashMap<>();

            for (String query : queryStrings) {
                String[] queries = query.split("=");
                queryKeyAndValues.put(queries[0], queries[1]);
            }

            return queryKeyAndValues;
        }

        return Map.of();
    }

    private String parseContentType(String headerAccept) {
        if (headerAccept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }
        return headerAccept.split(",")[0];
    }

    private String parseFilePath(String resource) {
        // 쿼리 스트링이 있는 경우
        if (resource.contains("?")) {
            int questionIndex = resource.indexOf("?");
            return resource.substring(0, questionIndex) + ".html";
        }

        // 쿼리 스트링 없이 확장자로 주어지는 경우
        if (ALLOWED_EXTENSIONS.stream().anyMatch(resource::endsWith)) {
            return resource;
        }

        return resource + ".html";
    }
}
