package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (
                final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                final var writer = connection.getOutputStream()) {
            Http11Request http11Request = new Http11Request(bufferedReader);

            String resourcePath = http11Request.getUri().substring(1);

            if(resourcePath.contains("login")) {
                Map<String, String> stringStringMap = parseQuery(resourcePath);

                String account = stringStringMap.get("account");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);

                log.info("user: {}", user.get());
            }

            byte[] body = readFromResourcePath(resourcePath);

            if(body == null) {
                String notFoundBody = "<h1>404 Not Found</h1>";
                byte[] responseHeader = createNotFoundHeader(notFoundBody.getBytes(StandardCharsets.UTF_8));

                writer.write(responseHeader);
                writer.write(notFoundBody.getBytes(StandardCharsets.UTF_8));
                writer.flush();
                return;
            }

            byte[] responseHeader = createResponseHeader(http11Request, body);
            writer.write(responseHeader);
            writer.write(body);
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQuery(final String uri) {
        HashMap<String, String> queryMap = new HashMap<>();

        if(uri.contains("?")) {
            String queryString = uri.substring(uri.indexOf('?') + 1);

            String[] split = queryString.split("&");
            for (String query : split) {
                String[] splitQuery = query.split("=");
                queryMap.put(splitQuery[0], splitQuery[1]);
            }
        }

        return queryMap;
    }

    private byte[] createNotFoundHeader(final byte[] notFoundBody) {
        String responseHeader =
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html; charset=utf-8\r\n" +
                        "Content-Length: " + notFoundBody.length + "\r\n" +
                        "\r\n";

        return responseHeader.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] createResponseHeader(final Http11Request http11Request, final byte[] body) {
        String contentType = guessByFileExtension(http11Request.getUri());

        String header = "HTTP/1.1 200 OK" + " \r\n" +
        "Content-Type: " + contentType + ";charset=utf-8" + " \r\n" +
        "Content-Length: " + body.length + " \r\n" +
        "\r\n";

        return header.getBytes(StandardCharsets.UTF_8);
    }

    private String guessByFileExtension(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm") || path.equals("/") || path.contains("/login")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";

        return "application/json";
    }

    private byte[] readFromResourcePath(final String resourcePath) throws IOException {
        if(resourcePath.isEmpty()) {
            String response = "Hello world!";

            return response.getBytes();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("static/");

        if(resourcePath.contains("?")) {
            stringBuilder.append(resourcePath, 0, resourcePath.indexOf("?"));
            stringBuilder.append(".html");
        }

        if(!resourcePath.contains("?")) {
            stringBuilder.append(resourcePath);
        }

        String classPath = stringBuilder.toString();
        try (InputStream resourceAsStream = getClass().
                getClassLoader().
                getResourceAsStream(classPath)) {
            if(resourceAsStream == null) {
                return null;
            }

            return resourceAsStream.readAllBytes();
        }
    }
}
