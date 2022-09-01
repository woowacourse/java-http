package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
             final var outputStream = connection.getOutputStream();
             final var input = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = HttpRequest.from(input);
            HttpResponse response = null;

            String uri = request.getUri();
            if (uri.equals("/")) {
                response = main(request);
            } else if (uri.contains(".")) {
                response = getResource(request);
            } else if (uri.contains("/login")) {
                response = login(request);
            }

            outputStream.write(response.toResponseString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse main(HttpRequest request) {
        return HttpResponse.withoutLocation(
            request.getVersion(),
            "200 OK",
            request.getUri(),
            "Hello world!"
        );
    }

    private HttpResponse getResource(HttpRequest request) {
        return HttpResponse.withoutLocation(
            request.getVersion(),
            "200 OK",
            request.getUri(),
            readFile(request.getUri())
        );
    }

    private HttpResponse login(HttpRequest request) {
        String uri = request.getUri();

        if (uri.equals("/login")) {
            return HttpResponse.withoutLocation(
                request.getVersion(),
                "200 OK",
                request.getUri(),
                readFile("/login.html")
            );
        }

        String queryString = uri.substring(uri.indexOf('?') + 1);
        Map<String, String> parsedQuery = parseQueryString(queryString);

        String account = parsedQuery.get("account");
        String password = parsedQuery.get("password");

        User user = InMemoryUserRepository.findByAccount(account).get();

        if (user.checkPassword(password)) {
            return HttpResponse.withLocation(
                request.getVersion(),
                "302 Found",
                request.getUri(),
                "/index.html",
                ""
            );
        } else {
            return HttpResponse.withLocation(
                request.getVersion(),
                "302 Found",
                request.getUri(),
                "/401.html",
                ""
            );
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> queryMap = new HashMap<>();

        for (String q : query.split("&")) {
            String[] parsedQuery = q.split("=");
            queryMap.put(parsedQuery[0], parsedQuery[1]);
        }

        return queryMap;
    }

    private String readFile(String uri) {
        try {
            final Path path = Paths.get(getClass()
                .getClassLoader()
                .getResource("static" + uri)
                .toURI());

            final List<String> contents = Files.readAllLines(path);

            StringBuilder result = new StringBuilder();
            for (String content : contents) {
                result.append(content + "\n");
            }

            return result.toString();
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
