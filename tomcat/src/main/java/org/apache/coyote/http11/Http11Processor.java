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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_EXTENSION_SEPARATOR = ".";

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
            handleRequest(reader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final BufferedReader reader, final OutputStream outputStream) throws IOException {
        try {
            final HttpRequest httpRequest = new HttpRequest(reader);
            httpRequest.parseHttpRequest();
            final String requestPath = httpRequest.getRequestPath();

            if (requestPath.equals("/")) {
                final HttpResponse response = HttpResponse.createWelcomeHttpResponse();
                sendHttpResponse(response, outputStream);
                return;
            }

            if (requestPath.equals("/login")) {
                final URL resource = getStaticResource("/login.html");
                final HttpResponse response = getHttpResponse(HttpStatusCode.OK, resource);
                sendHttpResponse(response, outputStream);
                logUserInformationIfExists(httpRequest);
                return;
            }

            final URL resource = getStaticResource(httpRequest.getRequestPath());
            final HttpResponse response = getHttpResponse(HttpStatusCode.OK, resource);
            sendHttpResponse(response, outputStream);
            
        } catch (HttpStatusException e) {
            final HttpStatusCode statusCode = e.getStatusCode();
            final URL resource = getStaticResource("/" + statusCode.getStatusCode() + ".html");
            final HttpResponse errorResponse = getHttpResponse(statusCode, resource);
            sendHttpResponse(errorResponse, outputStream);
        }
    }

    private URL getStaticResource(final String path) {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new HttpStatusException(HttpStatusCode.NOT_FOUND);
        }
        return resource;
    }

    private void logUserInformationIfExists(final HttpRequest httpRequest) {
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

    private byte[] readFile(final URL resource) throws IOException {
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private HttpResponse getHttpResponse(final HttpStatusCode statusCode, final URL resource) throws IOException {
        final String responseLine = String.format("HTTP/1.1 %s %s", statusCode.getStatusCode(),
                statusCode.getStatusMessage());
        final byte[] responseBody = readFile(resource);
        final LinkedHashMap<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", getContentType(resource));
        responseHeaders.put("Content-Length", String.valueOf(responseBody.length));

        return new HttpResponse(responseLine, responseHeaders, responseBody);
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.parseHttpResponse();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String getContentType(final URL resource) {
        final int extensionIndex = resource.getPath().lastIndexOf(RESOURCE_EXTENSION_SEPARATOR);
        final String responseResourceExtension = resource.getPath().substring(extensionIndex + 1);
        return HttpResponse.getContentType(responseResourceExtension);
    }
}
