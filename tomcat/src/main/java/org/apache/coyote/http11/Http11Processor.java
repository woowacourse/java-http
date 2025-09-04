package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            final var httpRequest = getHttpRequest(inputStream);

            printLoginAccount(httpRequest);

            final var responseBody = getResponseBody(httpRequest.getResourcePath());

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(httpRequest.getResourcePath()) + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(final InputStream inputStream) {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            final var line = bufferedReader.readLine();
            final var chunks = line.split(" ");

            return new HttpRequest(chunks[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printLoginAccount(final HttpRequest request) {
        if(!Objects.equals(request.getResourcePath(), "/login")) {
            return;
        }
        String account = request.getQueryParameter("account");

        if(account == null) {
            return;
        }
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("account " + account + " not found"));

        log.info("user : {}", user);
    }

    private String getResponseBody(final String resourcePath) {
        if (resourcePath.equals("/")) {
            return "Hello world!";
        }

        final var resource = ClassLoader.getSystemResource(getResolvedPath(resourcePath));

        if (resource == null) {
            return "Not found: " + resourcePath;
        }
        try {
            final var path = Paths.get(resource.getPath());

            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResolvedPath(final String resourcePath) {
        if (resourcePath.contains(".")) {
            return "static" + resourcePath;
        }
        return "static" + resourcePath + ".html";
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        // 이외의 MIME 타입 저장 가능
        return "text/html;charset=utf-8";
    }
}
