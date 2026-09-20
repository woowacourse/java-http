package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_DIRECTORY = "static/";
    private static final String DEFAULT_PAGE = "index.html";
    private static final String LOGIN_PATH = "login";
    private static final String LOGIN_PAGE = "login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

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

            final String uri = readRequestUri(inputStream);
            final String path = extractPath(uri);
            final Map<String, String> params = parseQueryString(extractQueryString(uri));

            if (isLoginRequest(path, params)) {
                write(outputStream, redirectResponse(loginLocation(params)));
                return;
            }
            write(outputStream, staticResourceResponse(resourcePath(path)));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequestUri(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine().split(" ")[1].substring(1);
    }

    private String extractPath(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private String extractQueryString(final String uri) {
        final int index = uri.indexOf("?");
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private boolean isLoginRequest(final String path, final Map<String, String> params) {
        return path.equals(LOGIN_PATH) && params.containsKey("account");
    }

    private String loginLocation(final Map<String, String> params) {
        final User existUser = InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(user -> user.checkPassword(params.get("password")))
                .orElse(null);
        if (existUser == null) {
            return UNAUTHORIZED_PAGE;
        }
        log.info("user : {}", existUser);
        return INDEX_PAGE;
    }

    private String redirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "",
                "");
    }

    private String resourcePath(final String path) {
        if (path.isEmpty()) {
            return DEFAULT_PAGE;
        }
        if (path.equals(LOGIN_PATH)) {
            return LOGIN_PAGE;
        }
        return path;
    }

    private String staticResourceResponse(final String path) throws IOException, URISyntaxException {
        final byte[] responseBody = readStaticResource(path);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentTypeOf(path) + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody));
    }

    private byte[] readStaticResource(final String path) throws IOException, URISyntaxException {
        final URL url = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + path);
        return Files.readAllBytes(Path.of(url.toURI()));
    }

    private String contentTypeOf(final String path) {
        final String extension = path.substring(path.lastIndexOf(".") + 1);
        if (extension.equals("css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private void write(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
