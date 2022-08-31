package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            String uri = httpRequest.get("request URI");

            String path = getPath(uri);
            Map<String, String> querys = getQueryParams(uri);
            Optional<User> user = InMemoryUserRepository.findByAccount(querys.getOrDefault("account", ""));
            user.ifPresent(value -> log.info(value.getAccount()));

            String responseBody = getResponseBody(path);
            String contentType = getContentType(path);

            HttpResponse httpResponse = HttpResponse.from("HTTP/1.1", "200 OK", contentType, responseBody);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(String uri) {
        int index = uri.indexOf("?");

        if (index != -1) {
            return uri.substring(0, index);
        }
        return uri;
    }

    private Map<String, String> getQueryParams(String uri) {
        int index = uri.indexOf("?");
        if (index != -1) {
            String queryString = uri.substring(index + 1);

            return Arrays.stream(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
        }
        return Map.of();
    }

    private String getResponseBody(String input) throws URISyntaxException, IOException {
        String responseBody = "Hello world!";

        if (!input.equals("/")) {
            if (!input.endsWith("css") && !input.endsWith("html") && !input.endsWith("js")) {
                input = input + ".html";
            }
            Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + input)
                .toURI());

            responseBody = new String(Files.readAllBytes(path));
        }

        return responseBody;
    }

    private String getContentType(String uri) {
        String contentType = "text/html;charset=utf-8";

        if (uri.endsWith("css")) {
            contentType = "text/css,*/*;q=0.1";
        }
        return contentType;
    }
}
