package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "text/javascript"
    );

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = new HttpRequest(reader);
            httpRequest.parseHttpRequest();
            final String requestPath = httpRequest.getRequestPath();

            URL resource = null;

            if (requestPath.equals("/login")) {
                resource = getStaticResource("/login.html");

                if (httpRequest.isQueryStringExists()) {
                    final Map<String, String> parameters = httpRequest.getQueryParameters();
                    final String account = parameters.get("account");
                    final String password = parameters.get("password");
                    InMemoryUserRepository.findByAccount(account)
                            .ifPresent(user -> {
                                if (user.checkPassword(password)) {
                                    log.info("user: " + user);
                                }
                            });
                }
            }

            if (!requestPath.equals("/") && !requestPath.equals("/login")) {
                resource = getStaticResource(httpRequest.getRequestUri());
            }

            HttpResponse response = HttpResponse.createWelcomeHttpResponse();
            if (resource != null) {
                final String responseLine = "HTTP/1.1 200 OK";
                final String responseBody = readFile(resource);
                final Map<String, String> responseHeaders = Map.of(
                        "Content-Type", getMimeType(resource),
                        "Content-Length", String.valueOf(responseBody.length())
                );

                response = new HttpResponse(responseLine, responseHeaders, responseBody);
            }

            String parsedResponse = response.parseHttpResponse();
            outputStream.write(parsedResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getStaticResource(final String path) throws FileNotFoundException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new FileNotFoundException();
        }
        return resource;
    }

    private String readFile(final URL resource) throws IOException {
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getMimeType(final URL resource) throws IOException {
        final String responseResourceExtension = resource.getPath().split("\\.")[1];
        return MIME_TYPES.get(responseResourceExtension);
    }
}
