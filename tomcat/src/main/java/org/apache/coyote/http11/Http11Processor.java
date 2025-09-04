package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static final int REQUEST_URL_INDEX = 1;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = parseRequestURL(httpRequestReader.readLine());

            sendResponse(requestLine, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestURL(final String requestLine) throws IOException {
        return requestLine.split(" ")[REQUEST_URL_INDEX];
    }

    private void sendResponse(final String requestURL, final OutputStream outputStream) throws IOException {
        final Path resourcePath = getResourcePath(requestURL);
        final String response = createHttpResponse(resourcePath);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Path getResourcePath(final String requestURL) {
        if (requestURL.equals("/")) {
            return getResourcePath("/index.html");
        }
        final URL resource = getClass().getClassLoader().getResource("static" + requestURL);
        if (resource == null) {
            return getResourcePath("/404.html");
        }
        return Paths.get(resource.getFile());
    }

    private String createHttpResponse(final Path resourcePath) throws IOException {
        final String responseBody = new String(Files.readAllBytes(resourcePath));
        final String contentType = Files.probeContentType(resourcePath);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
