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
            final String response = buildResponse(uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File resolveFile(String uri) {
        if (uri.contains("/login")) {
            final var params = queryParams(uri);
            validateUser(params);
            return getFile("/login.html");
        }
        return getFile(uri);
    }

    private void validateUser(Map<String, String> params) {
        InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(user -> user.checkPassword(params.get("password")))
                .ifPresent(user -> log.info("user: {}", user));
    }

    private String buildResponse(final String uri) throws IOException {
        final File file = resolveFile(uri);
        final String response = buildResponse(file);
        return response;
    }

    private String buildResponse(final File file) throws IOException {
        final String contentType = getContentType(file);
        final String responseBody = buildResponseBody(file);

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
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

    @Nullable
    private File getFile(final String uri) {
        if (uri.equals("/")) {
            return null;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return Optional.ofNullable(resource.getFile()).map(File::new).get();
    }

}
