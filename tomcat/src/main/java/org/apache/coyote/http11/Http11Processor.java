package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String requestLine = bufferedReader.readLine();

            String responseBody = "Hello world!";
            String contentType = "text/html";

            if (requestLine != null) {
                String uri = requestLine.split(" ")[1];

                String path = uri;
                String queryString = "";
                int index = uri.indexOf("?");

                if (index != -1) {
                    path = uri.substring(0, index);
                    queryString = uri.substring(index + 1);
                }

                if (path.equals("/login") && !queryString.isEmpty()) {
                    final Map<String, String> params = parseQueryString(queryString);
                    InMemoryUserRepository.findByAccount(params.get("account"))
                            .filter(user -> user.checkPassword(params.get("password")))
                            .ifPresent(user -> log.info("{}", user));
                }

                if (!path.equals("/")) {
                    String resourcePath = toResourcePath(path);
                    final URL resource = getClass().getClassLoader().getResource(resourcePath);
                    if (resource != null) {
                        responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                        contentType = getContentType(resourcePath);
                    }
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String string) {
        if (string.endsWith(".css"))
            return "text/css";
        if (string.endsWith(".js"))
            return "application/javascript";
        return "text/html";
    }

    private String toResourcePath(final String path) {
        if (path.contains(".")) {
            return "static" + path;
        }
        return "static" + path + ".html";
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        for (final String parameter : queryString.split("&")) {
            final String[] keyAndValue = parameter.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return params;
    }
}
