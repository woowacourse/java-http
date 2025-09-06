package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.techcourse.exception.ErrorMessage.*;

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
             final var outputStream = connection.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String uri = parseUri(br);
            if (uri.startsWith("/login")) {
                handleLogin(uri);
                respondStaticResource(Paths.get("/login.html"), outputStream);
                return;
            }
            respondStaticResource(Paths.get(uri), outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException(INVALID_REQUEST_LINE.getMessage());
        }
        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            throw new IllegalArgumentException(INVALID_HTTP_REQUEST_FORMAT.getMessage());
        }
        return parts[1];
    }

    private void handleLogin(String uri) {
        if (!uri.contains("?")) {
            return;
        }
        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);
        Map<String, String> params = new HashMap<>();
        parseQueryString(queryString, params);

        String account = params.get("account");
        String password = params.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND.getMessage()));
        user.logUserInfo(password, log);
    }

    private void parseQueryString(String queryString, Map<String, String> params) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            }
        }
    }

    private void respondStaticResource(Path path, OutputStream outputStream) throws IOException, URISyntaxException {
        String contentType = getContentType(path);
        final var responseBody = getResponseBodyFromStaticResource(path);
        final var response = formatHttpResponse(contentType, responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String getContentType(Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "text/html";
        }
        return contentType;
    }

    private String getResponseBodyFromStaticResource(Path path) throws IOException, URISyntaxException {
        if (path.equals(Path.of("\\"))) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(getStaticPath(path)));
    }

    private Path getStaticPath(Path path) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource("static" + path).toURI());
    }

    private String formatHttpResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
