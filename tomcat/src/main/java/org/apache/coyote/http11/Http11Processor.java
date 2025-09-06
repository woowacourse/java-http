package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<Integer, String> HTTP_STATUS_CODES = Map.ofEntries(
            Map.entry(200, "200 OK"),
            Map.entry(400, "400 Bad Request"),
            Map.entry(404, "404 Not Found")
    );
    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry("html", "text/html; charset=UTF-8"),
            Map.entry("css", "text/css; charset=UTF-8"),
            Map.entry("js", "application/javascript; charset=UTF-8"),
            Map.entry("ico", "image/x-icon")
    );

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String[] request = bufferedReader.readLine().split(" ");
            final String requestUri = request[1];

            Map<String, String> queryMap = extractQueryParams(requestUri);

            String path = requestUri;
            if (path.contains("?")) {
                path = path.split("\\?")[0];
            }
            if (!requestUri.contains(".")) {
                path = path + ".html";
            }
            log.debug("resource : {}", path);

            final URL resource = getClass().getClassLoader().getResource("static" + path);
            if (resource == null) {
                final URL notFoundPage = getClass().getClassLoader().getResource("static/404.html");
                final String response = generateResponse(404, notFoundPage);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final String response = generateResponse(200, resource);
            if ("/login.html".equals(path) && !queryMap.isEmpty()) {
                login(queryMap);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }

        final String[] split = uri.split("\\?");
        final String queryString = split.length > 1 ? split[1] : "";
        return parseQueryString(queryString);
    }

    private String generateResponse(final int httpStatusCode, final URL resource) throws IOException {
        final String resourceName = resource.getFile();
        final String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);
        final String responseBody = Files.readString(new File(resourceName).toPath());

        return String.join("\r\n",
                "HTTP/1.1 " + HTTP_STATUS_CODES.get(httpStatusCode) + " ",
                "Content-Type: " + MIME_TYPES.get(extension) + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryMap = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return queryMap;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=");
            final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = "";
            if (keyValue.length > 1) {
                value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
            queryMap.put(key, value);
        }
        return queryMap;
    }

    private void login(final Map<String, String> queryMap) {
        final String account = queryMap.get("account");
        final String password = queryMap.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
        }
    }
}
