package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_FILE_PATH = "static/404.html";

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String requestStartLine = bufferedReader.readLine();
            HttpRequest httpRequest = HttpRequest.from(requestStartLine);
            writeResponse(outputStream, httpRequest);

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRoot()) {
            writeRootResponse(outputStream);
            return;
        }
        writeResource(outputStream, httpRequest);
    }

    private void writeRootResponse(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        writeHttpResponse(outputStream, getContentType(responseBody + ".html"), responseBody);
    }

    private void writeResource(OutputStream outputStream, HttpRequest httpRequest) throws IOException {
        URL resource = getClass().getClassLoader().getResource(httpRequest.findTargetPath());

        if (resource == null) {
            writeNotFoundResource(outputStream);
            return;
        }

        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        writeHttpResponse(outputStream, getContentType(resource.getPath()), responseBody);
    }

    private void writeNotFoundResource(OutputStream outputStream) throws IOException {
        URL resource = getClass().getClassLoader().getResource(NOT_FOUND_FILE_PATH);

        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        writeHttpResponse(outputStream, getContentType(resource.getPath()), responseBody);
    }

    private void writeHttpResponse(OutputStream outputStream, String contentType, String responseBody) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType +";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
    }

    private String getContentType(String resource) {
        if (resource.endsWith(".html")) {
            return "text/html";
        }
        if (resource.endsWith(".css")) {
            return "text/css";
        }
        return "";
    }
}
