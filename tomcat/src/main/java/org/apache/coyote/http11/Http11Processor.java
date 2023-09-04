package org.apache.coyote.http11;

import nextstep.RequestFile;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private InMemoryUserRepository userRepository;

    public Http11Processor(final Socket connection, final InMemoryUserRepository userRepository) {
        this.connection = connection;
        this.userRepository = userRepository;
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

            final String parsedUri = parseFileName(inputStream);
            final Map<String, String> queryStrings = parseQueryStrings(parsedUri);
            String response;
            if (!queryStrings.isEmpty()) {
                log.info("queryStrings = " + queryStrings);
                if (isValidUser(queryStrings)) {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /index.html ",
                            ""
                    );
                } else {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /401.html ",
                            "");
                }
            } else {
                final RequestFile requestFile = RequestFile.getFile(parsedUri);
                final URL url = getClass().getClassLoader().getResource(requestFile.getFilePath());
                final var responseBody = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
                response = String.join("\r\n",
                        requestFile.getResponseHeader() +
                                "Content-Type: " + requestFile.getContentType(),
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isValidUser(final Map<String, String> queryStrings) {
        final User account = userRepository.findByAccount(queryStrings.get("account"))
                                           .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));
        return account.checkPassword(queryStrings.get("password"));
    }

    private Map<String, String> parseQueryStrings(final String parsedUri) {
        if (!parsedUri.contains("?")) {
            return Collections.emptyMap();
        }
        String queryStringUri;
        final int index = parsedUri.indexOf("?");
        queryStringUri = parsedUri.substring(index + 1);
        final String[] strings = queryStringUri.split("&");
        Map<String, String> queryStrings = new HashMap<>();
        for (final String string : strings) {
            final String[] keyValue = string.split("=");
            queryStrings.put(keyValue[0], keyValue[1]);
        }
        return queryStrings;
    }

    private String parseFileName(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            stringBuilder.append(line)
                         .append("\r\n");
        }

        return stringBuilder.toString()
                            .split(" ")[1];
    }
}
