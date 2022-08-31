package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.header.StatusCode;
import org.apache.coyote.response.Response;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final String requestUri = parseUri(reader);
            log.info("request uri : {}", requestUri);

            if (reader.readLine() == null) {
                return;
            }

            final Map<String, String> header = parseHeader(reader);
            final String response = getResponse(requestUri, header).toText();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseUri(final BufferedReader reader) throws IOException {
        return reader.readLine().split(" ")[1];
    }

    private Map<String, String> parseHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = reader.readLine();
        while (!"".equals(line)) {
            final String[] splitLine = line.split(": ", 2);
            header.put(splitLine[0], splitLine[1]);
            line = reader.readLine();
        }
        return header;
    }

    private String parseContentType(final Map<String, String> header) {
        return header
                .getOrDefault("Accept", "text/html")
                .split(",")[0];
    }

    private Response getResponse(final String requestUri, final Map<String, String> header)
            throws URISyntaxException, IOException {
        final String path = parsePath(requestUri);
        final Map<String, String> queryParams = parseQueryParams(requestUri);

        if (path.equals("/")) {
            return new Response(ContentType.HTML, StatusCode.OK, "Hello world!");
        }

        if (path.contains(".")) {
            return new Response(ContentType.of(parseContentType(header)), StatusCode.OK, getResource("static" + path));
        }

        if (path.equals("/login")) {
            login(queryParams.getOrDefault("account", ""), queryParams.getOrDefault("password", ""));
            return new Response(ContentType.HTML, StatusCode.OK, getResource("static/login.html"));
        }

        return new Response(ContentType.HTML, StatusCode.NOT_FOUND, getResource("static/404.html"));
    }

    private String parsePath(final String requestUri) {
        final int queryStartIndex = requestUri.indexOf("?");
        if (queryStartIndex < 0) {
            return requestUri;
        }
        return requestUri.substring(0, queryStartIndex);
    }

    private Map<String, String> parseQueryParams(final String requestUri) {
        final int queryStartIndex = requestUri.indexOf("?");
        if (queryStartIndex < 0) {
            return new HashMap<>();
        }

        final String queryString = requestUri.substring(queryStartIndex + 1);
        return Arrays.stream(queryString.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1]
                ));

    }

    private String getResource(final String resourcePath) throws URISyntaxException, IOException {
        final URI resource = getClass()
                .getClassLoader()
                .getResource(resourcePath)
                .toURI();
        return new String(Files.readAllBytes(Path.of(resource)));
    }

    private void login(final String account, final String password) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent((user) -> {
                    if (user.checkPassword(password)) {
                        System.out.println(user);
                    }
                });
    }
}
