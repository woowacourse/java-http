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

            final String path = requests.get("Path");

            if (!path.equals("/")) {
                responseBody = makeResponseBody(path);
            }

            final String contentType = parseContentType(requests.getOrDefault("Accept", ""));

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

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

    private String makeResponseBody(String resource) throws URISyntaxException, IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        String filePath = "";

        if (resource.contains("?")) {
            int questionIndex = resource.indexOf("?");
            filePath = resource.substring(0, questionIndex) + ".html";

            String queryString = resource.substring(questionIndex + 1);
            String[] queryStrings = queryString.split("&");
            Map<String, String> queryKeyAndValues = new HashMap<>();

            for (String query : queryStrings) {
                String[] queries = query.split("=");
                queryKeyAndValues.put(queries[0], queries[1]);
            }

            String account = queryKeyAndValues.get("account");

            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
            log.info("user:{}", user);
        }

        if (ALLOWED_EXTENSIONS.stream().anyMatch(resource::endsWith)) {
            filePath = resource;
        }

        final URL url = classLoader.getResource("static" + filePath);
        if (url == null) {
            throw new IOException("파일이 존재하지 않습니다.");
        }
        
        final File resourceFile = new File(Objects.requireNonNull(url).toURI());
        final Path path = resourceFile.toPath();

        return new String(Files.readAllBytes(path));
    }

    private String parseContentType(String headerAccept) {
        if (headerAccept.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }
        return headerAccept.split(",")[0];
    }
}
