package org.apache.coyote.http11;

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
import java.nio.file.Paths;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            final String requestMessage = parseRequestMessage(bufferedReader);
            final SimpleHttpRequest httpRequest = new SimpleHttpRequest(requestMessage);
            sendResponse(outputStream, httpRequest);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestMessage(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        do {
            stringBuilder.append(line).append("\r\n");
            line = reader.readLine();
        } while (line != null && line.isEmpty());

        return stringBuilder.toString();
    }

    private void sendResponse(final OutputStream outputStream, final SimpleHttpRequest httpRequest) throws URISyntaxException, IOException {
        if (httpRequest.getRequestUri().equals("/")) {
            final String responseBody = "Hello world!";
            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "", responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        }

        final File file = getStaticFile(httpRequest.getRequestUri());
        if (file == null) {
            sendNotFoundResponse(outputStream);
            return;
        }

        final String responseBody = readFileContent(file);
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                buildResponseContentTypeHeaderLine(httpRequest.parseStaticFileExtensionType()),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "", responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private File getStaticFile(final String fileName) throws URISyntaxException {
        final URL resource = getClass().getResource("/static" + fileName);
        if (resource == null) {
            return null;
        }
        return Paths.get(resource.toURI()).toFile();
    }

    private String readFileContent(final File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void sendNotFoundResponse(final OutputStream outputStream) throws URISyntaxException, IOException {
        final File file = getStaticFile("/404.html");
        final String responseBody = String.join("", readFileContent(file));
        final String response = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "", responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String buildResponseContentTypeHeaderLine(final FileExtensionType fileExtensionType) {
        String prefix = "Content-Type: ";
        if (fileExtensionType == FileExtensionType.CSS) {
            return prefix + HttpAcceptHeaderType.CSS.getValue();
        }

        if (fileExtensionType == FileExtensionType.JAVASCRIPT) {
            return prefix + HttpAcceptHeaderType.JAVASCRIPT.getValue();
        }

        if (fileExtensionType == FileExtensionType.SVG) {
            return prefix + HttpAcceptHeaderType.SVG.getValue();
        }

        return prefix + HttpAcceptHeaderType.HTML.getValue() + ";charset=utf-8";
    }
}
