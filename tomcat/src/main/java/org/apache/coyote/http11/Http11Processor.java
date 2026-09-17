package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        try {
            process(connection);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(final Socket connection) throws URISyntaxException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String uri = getUri(bufferedReader);
            if (uri.equals("/favicon.ico")) {
                return;
            }

            int index = uri.indexOf("?");
            String path = findPath(uri, index);
            Map<String, String> queryParams = findQueryString(uri, index);

            final Path filePath = getPath(path);
            final String responseBody = findResponseBody(filePath);

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: "+ findContentType(filePath) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            loggingUser(queryParams);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getUri(BufferedReader bufferedReader) throws IOException {
        String[] header = bufferedReader.readLine().split(" ");
        return header[1];
    }

    private String findPath(String uri, int index) {
        String path = uri;
        if (index != -1) {
            path = uri.substring(0, index);
        }

        if (!path.isBlank()) {
            if (!path.equals("/") && !path.contains(".")) {
                path += ".html";
            }
        }

        return "static" + path;
    }

    private Map<String, String> findQueryString(String uri, int index) {
        String queryString = "";
        if (index != -1) {
            queryString = uri.substring(index + 1);
        }

        Map<String, String> queryParams = new HashMap<>();
        if (!queryString.isEmpty()) {
            for (String query : queryString.split("&")) {
                String[] q = query.split("=");
                queryParams.put(q[0], q[1]);
            }
        }

        return queryParams;
    }

    private Path getPath(String path) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(path);
        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

    private String findContentType(Path filePath) {
        if (filePath.toString().endsWith(".css")) {
            return "text/css";
        } else if (filePath.toString().endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private String findResponseBody(Path filePath) throws IOException {
        if (Files.isDirectory(filePath)) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }

    private void loggingUser(Map<String, String> queryParams) {
        if (!queryParams.isEmpty()) {
            User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(IllegalArgumentException::new);
            log.info("user: {}", user);
        }
    }
}
