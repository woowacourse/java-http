package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.catalina.resource.MimeType;
import org.apache.catalina.resource.StaticResourceLoader;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_PAGE = "index.html";
    private static final String HTML_EXTENSION = ".html";

    private final Socket connection;
    private final StaticResourceLoader resourceLoader;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceLoader = new StaticResourceLoader();
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

            HttpServletRequest request = new Http11RequestParser(inputStream).parse();
            String path = resolvePath(request.requestLine().getUri().getPath());

            byte[] body = resolveRequest(request);

            MimeType mimeType = MimeType.fromPath(path);
            String header = createHeader(mimeType, body);

            outputStream.write(header.getBytes(StandardCharsets.ISO_8859_1));
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] resolveRequest(HttpServletRequest request) throws IOException {
        final URI uri = request.requestLine().getUri();
        final String path = uri.getPath();

        if (path.equals("/login")) {
            logIfLoginSucceeds(request.requestLine().getQueryParam());
        }
        return resourceLoader.load(resolvePath(path));
    }

    private void logIfLoginSucceeds(QueryParam queryParam) {
        final Optional<String> account = queryParam.get("account");
        final Optional<String> password = queryParam.get("password");
        if (account.isEmpty() || password.isEmpty()) {
            return;
        }

        InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()))
                .ifPresent(user -> log.info("user : {}", user));
    }

    private static String createHeader(MimeType mimeType, byte[] body) {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + mimeType.value() + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n";
    }

    private String resolvePath(String path) {
        if (path.endsWith("/")) {
            return path + DEFAULT_PAGE;
        }
        if (hasExtension(path)) {
            return path;
        }
        return path + HTML_EXTENSION;
    }

    private boolean hasExtension(String path) {
        final String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return lastSegment.contains(".");
    }
}
