package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    // 요청 필드
    private String method;
    private String path;
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    // 응답 필드
    private byte[] httpResponse;
    private final StringBuilder bodyBuilder = new StringBuilder();

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
             final var outputStream = connection.getOutputStream()) {

            parseRequestLine(reader);
            parseHeaders(reader);

            if (method.equals("GET")) {
                handleGetRequest(outputStream);
                return;
            }
            if (method.equals("POST")) {
//                handlePostRequest(outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleGetRequest(OutputStream outputStream) throws IOException {
        if (path.equals("/login")) {
            logLoginResult(queryParameters);
            serveStaticFile( path + ".html");
            writeResponse(outputStream);
            return;
        }
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            serveStaticFile(path);
            writeResponse(outputStream);
            return;
        }
        if (path.equals("/")) {
            serverHomePage();
            writeResponse(outputStream);
        }
    }

    private void parseRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("EOF: No request line received");
        }

        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid line received: " + line);
        }

        method = parts[0];
        String[] pathParts = parts[1].split("\\?", 2);
        path = pathParts[0];

        if (pathParts.length > 1) {
            parseQueryParameters(pathParts[1]);
        }
    }

    private void parseQueryParameters(String queryString) {
        for (String param : queryString.split("&")) {
            if (param.isEmpty()) continue;

            String[] keyValue = param.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";
            queryParameters.put(key, value);
        }
    }

    private void parseHeaders(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(" ");
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    }

    private void logLoginResult(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("user: {}", user),
                        () -> log.info("로그인 실패: account={}", account)
                );
    }

    private void serveStaticFile(String requestUri) throws IOException {
        String contentType = findContentType(requestUri);

        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri));
        String body = Files.readString(Path.of(resource.getPath()), UTF_8);
        writeBody(body);

        httpResponse = createOkHttpResponse(contentType).getBytes(UTF_8);
    }

    private void serverHomePage() {
        writeBody("Hello world!");
        httpResponse = createOkHttpResponse(findContentType("/")).getBytes(UTF_8);
    }

    private String findContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void writeBody(String body) {
        bodyBuilder.append(body);
    }

    @Nonnull
    private String createOkHttpResponse(String contentType) {
        int contentLength = bodyBuilder.toString().getBytes(UTF_8).length;
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                bodyBuilder.toString()
        );
    }

    private void writeResponse(OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse);
        outputStream.flush();
    }
}
