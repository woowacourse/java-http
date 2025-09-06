package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.dto.ResourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

    private final Socket connection;
    private final HttpRequestReader httpRequestReader;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.httpRequestReader = new HttpRequestReader();
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

            HttpRequest httpRequest = httpRequestReader.read(inputStream);

            String path = httpRequest.path();
            if (path.startsWith("/login")) {
                Map<String, String> queries = httpRequest.queries();
                String account = queries.get("account");
                String password = queries.get("password");
                UserService.login(account, password);
            }

            var status = 200;
            var reason = "OK";
            var responseBody = findResponseBody(path);
            if (!responseBody.found()) {
                status = 404;
                reason = "NOT FOUND";
            }
            var contentType = findContentType(path);

            final var response = String.join(CRLF,
                    "HTTP/1.1 " + status + " " + reason,
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.body().getBytes().length,
                    "",
                    responseBody.body());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResourceResult findResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return ResourceResult.found("Hello world!");
        }
        if (!uri.contains(".")) {
            uri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource == null) {
            return ResourceResult.notFound();
        }
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return ResourceResult.found(content);
    }

    private String findContentType(final String uri) {
        String encoding = ";charset=utf-8";
        if (uri.endsWith(".css")) {
            return "text/css" + encoding;
        }
        if (uri.endsWith(".js")) {
            return "application/javascript" + encoding;
        }
        if (uri.endsWith(".png")) {
            return "image/png";
        }
        return "text/html" + encoding;
    }
}
