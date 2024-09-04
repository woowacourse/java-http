package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

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
            String path = readPath(inputStream);
            if (path.equals("/")) {
                processHome(outputStream);
            }
            processFiles(outputStream, path);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readPath(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();

        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }

    private String readFileType(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
            return "";
        }
        return path.substring(lastDotIndex + 1);
    }

    private void processHome(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void processFiles(OutputStream outputStream, String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        if (resource == null) {
            return;
        }
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + readFileType(path) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();

    }
}
