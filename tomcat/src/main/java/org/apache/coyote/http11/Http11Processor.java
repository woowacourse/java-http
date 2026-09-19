package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            final String request = readRequest(inputStream);

            String uri = request.split(" ")[1];
            log.info("uri: {}", uri);
            final String type = findType(uri);

            final String queryString = findQueryString(uri);
            if (!queryString.isBlank()) {
                uri = List.of(uri.split("\\?")).getFirst() + ".html";
                final Map<String, String> pairs = findQueries(queryString);
                userMatching(pairs.get("account"), pairs.get("password"));
            }

            final String responseBody = makeResponseBody(uri);
            final String response = makeResponse(type, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequest(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder builder = new StringBuilder();

        String line;
        while (!(line = reader.readLine()).isBlank()) {
            builder.append(line);
            builder.append("\r\n");
        }
        return builder.toString();
    }

    private String findType(String uri) {
        if (uri.contains(".")) {
            return List.of(uri.split("\\.")).getLast();
        }
        return "html";
    }

    private String findQueryString(String uri) {
        if (uri.contains("?")) {
            return List.of(uri.split("\\?")).getLast();
        }
        return "";
    }

    private Map<String, String> findQueries(String queryString) {
        final List<String> queries = List.of(queryString.split("&"));
        final Map<String, String> pairs = new HashMap<>();

        for (String query : queries) {
            String[] pair = query.split("=", 2);
            pairs.put(pair[0], pair[1]);
        }
        return pairs;
    }

    private void userMatching(String account, String password) {
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("user : {}", user));
    }

    private String makeResponseBody(String uri) throws IOException {
        if (uri.equals("/") || uri.isBlank()) {
            return "Hello world!";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);

        final String filePath = resource.getFile();
        final Path path = Paths.get(filePath);

        return Files.readString(path);
    }

    private String makeResponse(String type, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
