package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

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
    public void process(final Socket connection) throws UncheckedServletException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            List<String> header = new ArrayList<>();
            for (String data = bufferedReader.readLine(); data != null; data = bufferedReader.readLine()) {
                if (data.isBlank()) {
                    break;
                }
                header.add(data);
            }
            if (header.isEmpty()) {
                return;
            }

            // 엔드포인트 변경
            final String requestUri = header.getFirst();
            String endpoint = requestUri.split(" ")[1];

            int queryIndex = endpoint.indexOf("?");
            if (queryIndex != -1) {
                String queryString = endpoint.substring(queryIndex + 1);
                endpoint = endpoint.substring(0, queryIndex);
                checkLoginQuery(queryString);
            }
            List<String> fileExtensions = List.of(".css", ".ico", ".html", ".js");
            boolean existExtension = false;
            for (String extension : fileExtensions) {
                if (endpoint.endsWith(extension)) {
                    existExtension = true;
                    break;
                }
            }
            if (!existExtension && !endpoint.equals("/")) {
                endpoint += ".html";
            }

            String responseBody = makeResponseBody(endpoint);
            if (responseBody == null) {
                return;
            }
            String contentType = makeContentType(endpoint);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkLoginQuery(final String queryString) {
        Map<String, String> queryStorage = new HashMap<>();
        for (String query : queryString.split("&")) {
            String[] keyValue = query.split("=");
            queryStorage.put(keyValue[0], keyValue[1]);
        }

        User user = InMemoryUserRepository.findByAccount(queryStorage.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("해당 account가 존재하지 않습니다."));
        if (user.checkPassword(queryStorage.get("password"))) {
            log.info("user : {}", user);
        }
    }

    private String makeContentType(final String endpoint) {
        if (endpoint.length() > 3 && endpoint.endsWith("css")) {
                return "text/css";
            }

        return "text/html";
    }

    private String makeResponseBody(final String endpoint) throws URISyntaxException, IOException {
        if (endpoint.equals("/")) {
            return "Hello world!";
        }
        final URL resource = Http11Processor.class.getResource("/static" + endpoint);
        if (resource == null) {
            return null;
        }
        final Path path = Paths.get(resource.toURI()).toFile().toPath();
        return Files.readString(path);
    }
}
