package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import jakarta.activation.MimeTypeEntry;

import org.apache.coyote.HttpRequestParser;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Set<String> STATIC_RESOURCE_EXTENSIONS = Set.of("css", "js", "ico");
    private static final String STATIC_RESOURCE_ROOT_PATH = "static/";
    private static final String PATH_DELIMITER = "/";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
            final String response = processResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processResponse(final HttpRequest httpRequest) throws IOException {
        final String endPoint = httpRequest.uri().getPath();
        final String[] paths = endPoint.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return processRootResponse();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return processStaticResponse(resourceName);
        }

        return processRootResponse();
    }

    private String processStaticResponse(final String resourceName) throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(resourceName));
        final Path resourcePath = Path.of(resourceURL.getPath());
        final String responseBody = Files.readString(resourcePath);
        final String mimeType = Files.probeContentType(resourcePath);

        return getResponse(mimeType, responseBody);
    }

    private String processRootResponse() {
        final String responseBody = "Hello world!";
        return getResponse("text/html", responseBody);
    }

    private String getResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType +";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResourcePath(final String resourcePath) {
        final String[] fileNames = resourcePath.split("\\.");
        final String extension = fileNames[1];

        if (STATIC_RESOURCE_EXTENSIONS.contains(extension)) {
            return STATIC_RESOURCE_ROOT_PATH.concat(extension).concat(PATH_DELIMITER).concat(resourcePath);
        }

        return STATIC_RESOURCE_ROOT_PATH.concat(resourcePath);
    }
}
