package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader headerReader = new BufferedReader(new InputStreamReader(inputStream,
                     StandardCharsets.UTF_8))) {
            final HttpRequestStartLine startLine = HttpRequestStartLine.from(headerReader);

            checkUser(startLine);
            final URL resourceUrl = getResourceUrl(startLine.getUri());

            final String responseBody = readContext(resourceUrl);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(startLine.getUri()) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String uri) {
        if (uri == null) {
            return "text/html";
        }

        if (uri.contains(".css")) {
            return "text/css";
        }

        if (uri.contains(".js")) {
            return "text/javascript";
        }

        return "text/html";
    }

    private void checkUser(HttpRequestStartLine startLineContents) {
        if (!startLineContents.getUri().contains("login")) {
            return;
        }

        final Map<String, String> queryParams = startLineContents.getQueryParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));

        if (user.isPresent()) {
            final User loginUser = user.get();
            logLoginUser(queryParams, loginUser);
        }
    }

    private void logLoginUser(Map<String, String> queryParams, User loginUser) {
        if (loginUser.checkPassword(queryParams.get("password"))) {
            log.info("user : " + loginUser);
        }
    }

    private URL getResourceUrl(String requestUri) {
        if (requestUri.equals("/")) {
            return null;
        }

        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }
        return getClass().getClassLoader().getResource("static" + requestUri);
    }

    private String readContext(final URL resourceUrl) throws IOException {
        if (resourceUrl == null) {
            return "Hello world!";
        }
        final File resourceFile = new File(resourceUrl.getFile());
        final Path resourcePath = resourceFile.toPath();
        final byte[] resourceContents = Files.readAllBytes(resourcePath);
        return new String(resourceContents);
    }
}
