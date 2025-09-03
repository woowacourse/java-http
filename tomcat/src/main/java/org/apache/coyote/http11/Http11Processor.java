package org.apache.coyote.http11;

import com.http.application.HttpRequestParser;
import com.http.application.RequestHandlerMapper;
import com.http.domain.HttpRequest;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final HttpRequest httpRequest = HttpRequestParser.parse(reader);

            final URL resourceUrl = getFileUrl(httpRequest);

            final byte[] responseBody = parseResponseBody(resourceUrl);

            final var responseHeaders = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + parseContentType(httpRequest), "Content-Length: " + responseBody.length + " ",
                    "", "");

            RequestHandlerMapper.handle(httpRequest);

            outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URL getFileUrl(HttpRequest httpRequest) {
        String path = httpRequest.startLine().path();

        if (!path.contains(".")) {
            path += ".html";
        }

        // '/'로 시작하는 경로를 제거하고 static 폴더 내에서 찾기
        String resourcePath = path.startsWith("/") ? path.substring(1) : path;
        resourcePath = "static/" + resourcePath;

        return getClass().getClassLoader().getResource(resourcePath);
    }

    private byte[] parseResponseBody(URL resourceUrl) throws IOException {
        if (resourceUrl == null) {
            return "Hello world!".getBytes();
        }

        return Files.readAllBytes(new File(resourceUrl.getFile()).toPath());
    }

    private String parseContentType(HttpRequest httpRequest) {
        final Map<String, String> headers = httpRequest.headers();
        if (!headers.containsKey("Accept")) {
            return "text/html;charset=utf-8 ";
        }

        return headers.get("Accept").split(",")[0];
    }
}
