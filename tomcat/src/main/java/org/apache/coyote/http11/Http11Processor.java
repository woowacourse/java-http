package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.RequestMappingHandler;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            RequestMappingHandler requestMappingHandler = new RequestMappingHandler();

            HttpRequest request = getHttpRequest(inputStream);
            HttpResponse response = requestMappingHandler.request(request);

            respond(response, outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return new HttpRequest(reader);
    }

    private void respond(HttpResponse response, OutputStream outputStream) throws IOException, URISyntaxException {
        final var responseText = createHttpResponse(response);
        outputStream.write(responseText.getBytes());
        outputStream.flush();
    }

    private String createHttpResponse(HttpResponse response) throws IOException, URISyntaxException {
        String responseBody = getResponseBody(response.getBody());
        String headerString = response.headersToString();
        StringBuilder responseBuilder = createResponseString(response, headerString, responseBody);
        return responseBuilder.toString();
    }

    private StringBuilder createResponseString(HttpResponse response, String headerString, String responseBody) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ")
                .append(response.getStatusCode().getValue()).append(" ")
                .append(response.getStatusCode()).append("\r\n");
        if (headerString != null) {
            responseBuilder.append(headerString).append("\r\n");
        }
        responseBuilder.append("Content-Type: ").append(response.getContentType()).append("\r\n");
        responseBuilder.append("Content-Length: ").append(responseBody.getBytes().length).append("\r\n\r\n");
        responseBuilder.append(responseBody);
        return responseBuilder;
    }

    private String getResponseBody(String path) throws IOException, URISyntaxException {
        URL resource = getResource(path);
        if (resource == null || Files.isDirectory(Path.of(resource.toURI()))) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL getResource(String path) {
        return getClass()
                .getClassLoader()
                .getResource("static" + path);
    }
}
