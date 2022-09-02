package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String uri = line.split(" ")[1];
            RequestUri requestUri = new RequestUri(uri);
            final String responseBody = getResponseBody(requestUri.getPath());
            final String contentType = getContentType(requestUri.getPath());
            final Map<String, String> queryParams = requestUri.getQueryParams();

            if (queryParams.containsKey("account")) {
                Optional<User> account = InMemoryUserRepository.findByAccount(queryParams.get("account"));
                if (account.isPresent()) {
                    User user = account.get();
                    log.info("user = {}", user);
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

    private String getResponseBody(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            requestUri = "/index.html";
        }
        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }
        URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        Path filePath = new File(resource.getPath()).toPath();
        return String.join("\r\n", Files.readAllLines(filePath));
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith("css")) {
            return "text/css";
        }
        if (requestUri.endsWith("js")) {
            return "application/javascript";
        }
        return "text/html";
    }
}
