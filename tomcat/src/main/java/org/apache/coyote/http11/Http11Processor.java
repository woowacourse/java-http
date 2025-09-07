package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.StatusLine;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_VERSION = "HTTP/1.1";

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
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequestParser httpRequestParser = new HttpRequestParser();
            final HttpRequest request = httpRequestParser.parse(reader);

            final URL resource = getResource(request.getPath());

            if ("/login".equals(request.getPath())) {
                final String account = request.getQueryParams().get("account");
                final String password = request.getQueryParams().get("password");

                if (account != null && password != null) {
                    InMemoryUserRepository.findByAccount(account)
                            .ifPresentOrElse(user -> {
                                if (user.checkPassword(password)) {
                                    log.info("user : {}", user);
                                } else {
                                    log.info("Login failed for account: {}", account);
                                }
                            }, () -> log.info("No such account: {}", account));
                }
            }

            final String responseBody;
            final StatusLine statusLine;

            if (resource != null) {
                responseBody = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
                statusLine = new StatusLine(HTTP_VERSION, StatusCode.OK);
            } else if ("/".equals(request.getPath())) {
                responseBody = "Hello world!";
                statusLine = new StatusLine(HTTP_VERSION, StatusCode.OK);
            } else {
                responseBody = "";
                statusLine = new StatusLine(HTTP_VERSION, StatusCode.NOT_FOUND);
            }

            final String contentType = getContentType(request.getPath());
            final HttpHeaders headers = getHttpHeaders(contentType, responseBody);
            final HttpResponse httpResponse = new HttpResponse(statusLine, headers, responseBody.getBytes());
            final String response = httpResponse.toString();

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getResource(final String path) {
        if (("/login".equals(path))) {
            return getClass().getClassLoader()
                    .getResource("static" + path + ".html");
        }

        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
            return getClass().getClassLoader()
                    .getResource("static" + path);
        }
        return getClass().getClassLoader().getResource(path);
    }

    private HttpHeaders getHttpHeaders(final String contentType, final String responseBody) {
        final HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", contentType);
        headers.addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        return headers;
    }

    private String getContentType(final String path) {
        if (path.endsWith(".html") || "/login".equals(path) || "/".equals(path)) {
            return "text/html;charset=utf-8";
        }

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "text/plain;charset=utf-8";
    }
}
