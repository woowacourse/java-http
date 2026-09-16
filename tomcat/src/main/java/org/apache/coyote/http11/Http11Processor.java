package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
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
            final var response = buildResponseWith(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponseWith(InputStream inputStream) throws IOException {
        String requestUri = parseRequestUriFrom(inputStream);

        String path = parsePathFrom(requestUri);
        String queryString = parseQueryStringFrom(requestUri);

        if(path.startsWith("/login")){
            return loginResponse(queryString);
        }

        String contentType = contentTypeOf(path);
        String responseBody = resolveContentOf(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String loginResponse(String queryString) {
        Optional<User> account = findAccount(queryString);

        if(account.isPresent()) {
            return String.join("\r\n",
                    "HTTP/1.1 302 FOUND ",
                    "Location: /index.html "
            );
        }

        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: /401.html "
        );
    }

    private Optional<User> findAccount(String queryString) {
        if (queryString == null) {
            return Optional.empty();
        }

        String[] queryParts = queryString.split("&");

        String accountQuery = queryParts[0].substring(queryParts[0].lastIndexOf("=") + 1);
        String passwordQuery = queryParts[1].substring(queryParts[1].lastIndexOf("=") + 1);

        Optional<User> account = InMemoryUserRepository.findByAccount(accountQuery);

        if(account.isPresent() && account.get().checkPassword(passwordQuery)) {
            log.info("user : " + account);
            return account;
        }

        return Optional.empty();
    }

    private String parseQueryStringFrom(String requestUri) {
        if (requestUri.contains("?")) {
            return requestUri.substring(requestUri.indexOf('?') + 1);
        }
        return null;
    }

    private String parseRequestUriFrom(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return bufferedReader.readLine().split(" ")[1];
    }

    private String parsePathFrom(String requestUri) {
        String path = requestUri;
        if (path.contains("?")) {
            int queryFileStrEndIndex = requestUri.indexOf("?");
            if (queryFileStrEndIndex != -1) {
                path = path.substring(0, queryFileStrEndIndex);
            }
        }

        if(!path.contains(".")) {
            path = path.concat(".html");
        }

        return path;
    }

    private String resolveContentOf(String filePath) throws IOException {
        URL resource = getResource(filePath);
        if (!filePath.equals("/") && resource != null) {
            return Files.readString(new File(resource.getFile()).toPath());
        }
        return "Hello world!";
    }

    private URL getResource(String filePath) {
        String path = "static" + filePath;
        return getClass().getClassLoader().getResource(path);
    }

    private String contentTypeOf(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }
}
