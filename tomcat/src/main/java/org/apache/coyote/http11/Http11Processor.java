package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

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
            byte[] responseBody = "Hello world!".getBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    headers,
                    responseBody
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
        final URL resource = getClass()
                .getClassLoader().getResource("static" + uri);

        if (resource == null) {
            return readResource(notFoundResource(), new HttpStatusLine(HTTP_VERSION,
                    404, "Not Found"));
        }

        return readResource(resource,
                new HttpStatusLine(HTTP_VERSION, 200, "OK"));
    }

    private URL notFoundResource() {
        return Objects.requireNonNull(
                getClass().getClassLoader().getResource("static/404.html"),
                "static/404.html이 존재하지 않습니다."
        );
    }

    private HttpResponse readResource(final URL resource, final HttpStatusLine statusLine) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            byte[] responseBody = inputStream.readAllBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, contentTypeOf(resource.getPath()));
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(statusLine, headers, responseBody);
        }
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
