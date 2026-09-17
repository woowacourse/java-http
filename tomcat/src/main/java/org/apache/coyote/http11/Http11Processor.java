package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final HttpRequestLine requestLine = HttpRequestLine.from(inputStream);
            log.info("requestLine = {}", requestLine);

            HttpResponse httpResponse = handle(requestLine);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequestLine requestLine) throws IOException {
        final String uri = requestLine.uri();

        if ("/".equals(uri)) {
            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    "text/html;charset=utf-8",
                    "Hello world!".getBytes(StandardCharsets.UTF_8)
            );
        }

        if ("/login".equals(uri)) {
            return login(requestLine.queryParameters());
        }

        return getStaticResource(uri);
    }

    private HttpResponse login(final Map<String, String> queryParameters) throws IOException {
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");

        if (account != null && password != null) {
            InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password))
                    .ifPresent(user -> log.info("user : {}", user));
        }

        return getStaticResource("/login.html");
    }

    private HttpResponse getStaticResource(final String uri) throws IOException {
        final String path = resolvePath(uri);

        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    contentTypeOf(path),
                    resource.readAllBytes()
            );
        }
    }

    private String resolvePath(String uri) {
        String path = "static" + uri;

        if (getClass().getClassLoader().getResource(path) == null) {
            return "static/404.html";
        }

        return path;
    }

    private String contentTypeOf(String path) {
        String contentType = URLConnection.guessContentTypeFromName(path);

        if (contentType == null) {
            return "application/octet-stream";
        }

        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        return contentType;
    }
}
