package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // 먼저 requestLine 줄을 읽어온 후 Headers를 처리할 수 있다.
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            log.info(requestLine);

            final Map<String, String> queryStrings = parseQueryStrings(requestLine);
            log.info("queryStrings: {}", queryStrings);
            final Map<String, String> headers = parseHeaders(reader);
            log.info("headers: {}", headers);

            String requestPath = parseRequestPath(requestLine);

            final URL resourceUrl = getFileUrl(requestPath);

            final byte[] responseBody = parseResponseBody(resourceUrl);

            final var responseHeaders = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + parseContentType(headers), "Content-Length: " + responseBody.length + " ",
                    "", "");

            handle(requestPath, queryStrings);

            outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getFileUrl(String requestPath) {
        if (requestPath == null || requestPath.endsWith("/")) {
            return null;
        }

        requestPath = requestPath.trim().split("\\?")[0];
        String resourcePath = "static" + (requestPath.startsWith("/") ? requestPath : "/" + requestPath);

        if (!resourcePath.contains(".")) {
            resourcePath += ".html";
        }

        log.info("패스 확인 : {}", requestPath);
        return getClass().getClassLoader().getResource(resourcePath);
    }

    private byte[] parseResponseBody(URL resourceUrl) throws IOException {
        if (resourceUrl == null) {
            return "Hello world!".getBytes();
        }

        return Files.readAllBytes(new File(resourceUrl.getFile()).toPath());
    }

    private String parseRequestPath(String requestLine) {
        if (requestLine == null) {
            return "/";
        }

        final String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        return requestParts[1].split("\\?")[0];
    }

    private String parseContentType(Map<String, String> headers) {
        if (!headers.containsKey("Accept")) {
            return "text/html;charset=utf-8 ";
        }

        return headers.get("Accept").split(",")[0];
    }

    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new java.util.ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }

        return headerLines.stream()
                .skip(1)
                .map(headerLine -> headerLine.split(": "))
                .filter(header -> header.length == 2)
                .collect(
                        Collectors.toMap(
                                header -> header[0],
                                header -> header[1]
                        )
                );
    }

    private Map<String, String> parseQueryStrings(String requestLine) {
        if (requestLine == null || !requestLine.contains("?")) {
            return Map.of();
        }

        String queryString = requestLine.split(" ")[1].split("\\?")[1];
        String[] queries = queryString.split("&");

        return Stream.of(queries)
                .map(query -> query.split("="))
                .filter(query -> query.length == 2)
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }

    private void handle(String requestPath, Map<String, String> queryStrings) {
        if (requestPath.equals("/login")) {
            String account = queryStrings.get("account");
            String password = queryStrings.get("password");
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

            if (user.checkPassword(password)) {
                log.info("user : {} ", user);
            }
        }
    }
}
