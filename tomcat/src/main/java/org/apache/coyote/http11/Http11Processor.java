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
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String uri = parts[1];

            String path = getPathByUri(uri);
            Map<String, String> queryString = getQueryStringByUri(uri);

            String contents = "";
            if ("GET".equals(method)) {
                contents = handleGetMethod(path, queryString);
            }

            final var responseBody = contents;

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(path),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPathByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private Map<String, String> getQueryStringByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Map.of();
        }
        String queryString = uri.substring(index + 1);
        return getQueryString(queryString);
    }

    private Map<String, String> getQueryString(final String querystring) {
        return Arrays.stream(querystring.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private String handleGetMethod(final String path, final Map<String, String> queryString) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!";
        }
        if ("/login".equals(path)) {
            User user = InMemoryUserRepository.findByAccount(queryString.get("account")).orElseThrow();
            if(user.checkPassword(queryString.get("password"))) {
                log.info("User{}", user);
            }
            return getFile("/login.html");
        }
        return getFile(path);
    }

    private String getFile(String path) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + path);

        return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
