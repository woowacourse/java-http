package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Charset CHARSET = StandardCharsets.UTF_8;

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
        try (final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            final List<String> requestHeaders = getRequestHeaders(bufferedReader);
            final String requestURI = getRequestURI(requestHeaders);

            final Map<String, String> queryStrings = parseQueryString(requestHeaders);
            checkLogin(requestURI, queryStrings);
            final String contentType = ContentType.findContentTypeByURI(requestURI);

            final byte[] responseBody = getResponseBody(requestURI);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=" + CHARSET.name().toLowerCase() + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody, CHARSET));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkLogin(final String requestURI, final Map<String, String> queryStrings) {
        if (!requestURI.equals("/login")) {
            return;
        }
        final String account = queryStrings.get("account");
        final String password = queryStrings.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정을 확인해주세요."));
        if (user.checkPassword(password)) {
            log.info("user = {}", user);
        }
    }

    private Map<String, String> parseQueryString(final List<String> requestHeaders) {
        final String requestURI = parseRequestURI(requestHeaders);
        Map<String, String> storage = new HashMap<>();
        if (!requestURI.contains("?")) {
            return storage;
        }
        final String splitQueryStrings = requestURI.split("\\?")[1];
        final String[] queryString = splitQueryStrings.split("&");
        for (final String str : queryString) {
            final String[] keyValue = str.split("=");
            if (keyValue.length < 2) {
                storage.put(keyValue[0], "");
                continue;
            }
            storage.put(keyValue[0], keyValue[1]);
        }
        return storage;
    }

    private byte[] getResponseBody(final String requestURI) throws IOException {
        if (requestURI.equals("/")) {
            return "Hello world!".getBytes();
        }
        return getNotDefaultPathResponseBody(requestURI);
    }

    private byte[] getNotDefaultPathResponseBody(final String requestURI) throws IOException {
        String makeRequestURI = "static" + requestURI;
        if (isNotStaticFile(requestURI)) {
            makeRequestURI += ".html";
        }
        final URL url = getUrl(makeRequestURI);
        final Path path = new File(url.getPath()).toPath();

        return Files.readAllBytes(path);
    }

    private URL getUrl(final String requestURI) {
        URL url = getClass().getClassLoader().getResource(requestURI);
        if (Objects.isNull(url)) {
            return getClass().getClassLoader().getResource("static/404.html");
        }
        return url;
    }

    private boolean isNotStaticFile(final String requestURI) {
        return !ContentType.checkFileExtension(requestURI);
    }

    private String getRequestURI(final List<String> requestHeaders) {
        String requestURI = parseRequestURI(requestHeaders);
        if (requestURI.contains("?")) {
            requestURI = requestURI.split("\\?")[0];
        }
        return requestURI;
    }

    private String parseRequestURI(final List<String> requestHeaders) {
        final String firstLineOfRequestHeaders = requestHeaders.get(0);
        final String[] splitFirstLine = firstLineOfRequestHeaders.split(" ");
        return splitFirstLine[1];
    }

    private List<String> getRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String temp;
        while (!Objects.equals(temp = bufferedReader.readLine(), "")) {
            if (temp == null) {
                break;
            }
            requestHeaders.add(temp);
        }
        return requestHeaders;
    }
}
