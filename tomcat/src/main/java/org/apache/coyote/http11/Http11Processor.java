package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
            final var bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final var uri = bufferedReader.readLine().split(" ")[1];
            log.info("request uri: {}", uri);
            final var response = handleRequest(uri);

            outputStream.write(response.buildResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleLogin(final String uri) throws IOException {
        final var params = queryParams(uri);
        final var user = findUser(params);
        if (user.isEmpty()) {
            File page = getFile("/401.html");
            String contentType = getContentType(page);
            String body = buildResponseBody(page);
            return new HttpResponse("401 Unauthorized", contentType, body);
        }
        log.info("user: {}", user.get());
        return new HttpResponse("302 Found", "Content-Type: text/plain;charset=utf-8 ", null,
                Map.of("Location", "/index.html"));
    }

    private String getContentType(final File file) throws IOException {
        if (file == null) {
            return "Content-Type: text/plain;charset=utf-8 ";
        }
        final var urlConnection = file.toURI().toURL().openConnection();
        final var mimeType = urlConnection.getContentType();
        return "Content-Type: " + mimeType + ";charset=utf-8 ";

    }

    private String buildResponseBody(final File file) throws IOException {
        if (file == null) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(file.toPath()));
    }


    private Optional<User> findUser(Map<String, String> params) {
        return InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(user -> user.checkPassword(params.get("password")))
                .stream().findFirst();
    }

    private HttpResponse handleRequest(final String uri) throws IOException {
        if (uri.contains("/login")) {
            return handleLogin(uri);
        }

        try {
            File file = getFile(uri);
            String contentType = getContentType(file);
            String body = buildResponseBody(file);
            return new HttpResponse("200 OK", contentType, body);
        } catch (Exception e) {
            File page = getFile("/404.html");
            String contentType = getContentType(page);
            String body = buildResponseBody(page);
            return new HttpResponse("404 Not Found", contentType, body);
        }

    }

    private Map<String, String> queryParams(String uri) {
        final var params = new HashMap<String, String>();
        int index = uri.indexOf("?");
        String queryString = uri.substring(index + 1);
        while (queryString.contains("&")) {
            String[] split = queryString.split("&");
            for (String s : split) {
                String[] keyValue = s.split("=");
                params.put(keyValue[0], keyValue[1]);
            }
            queryString = split[split.length - 1];
        }
        return params;
    }

    @Nullable
    private File getFile(final String uri) {
        if (uri.equals("/")) {
            return null;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return Optional.ofNullable(resource.getFile()).map(File::new).get();
    }

}
