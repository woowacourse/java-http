package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.apache.catalina.connector.CoyoteAdepter;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.ContentType;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_PATH = "static";

    private final Socket connection;
    private final Adapter adapter;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.adapter = new CoyoteAdepter();
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            final var httpRequest = getHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            String resourcePath = httpRequest.parseResourcePath();
            final var httpResponse = new Http11Response();
            httpResponse.setResourcePath(resourcePath);
            adapter.service(httpRequest, httpResponse);

            handleRequest(httpResponse);
            outputStream.write(httpResponse.getResponseLine());
            outputStream.write(httpResponse.getHeader());
            outputStream.write(httpResponse.getBody());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Http11Request getHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return null;
        }
        final var requestLines = requestLine.split(" ");
        final var resourcePath = requestLines[1];
        final var headers = getHeaders(bufferedReader);
        log.info("resourcePath: {}, header: {}", resourcePath, headers);
        return new Http11Request(requestLines[0], resourcePath, headers, null);
    }

    private LinkedHashMap<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            final var header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }
        return headers;
    }

    private void handleRequest(final Http11Response httpResponse)
            throws IOException, URISyntaxException {
        String resourcePath = httpResponse.getResourcePath();
        if (resourcePath.equals("/")) {
            buildRootResponse(httpResponse);
            return;
        }
        final var resource = getClass().getClassLoader().getResource(STATIC_PATH + resourcePath);
        final var filePath = Paths.get(Objects.requireNonNull(resource).toURI());

        final var contentType = ContentType.fromPath(resourcePath);
        httpResponse.addHeader("Content-Type", contentType.getValue());

        final var responseBody = Files.readAllBytes(filePath);
        httpResponse.addHeader("Content-Length", String.valueOf(responseBody.length));
        httpResponse.setBody(responseBody);
    }

    private void buildRootResponse(final Http11Response httpResponse) {
        byte[] body = "Hello world!".getBytes();
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.length));
        httpResponse.setBody(body);
    }
}
