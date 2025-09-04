package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            if (line == null || line.isBlank()) {
                writeError(outputStream, 400, "Bad Request", "Request line is empty");
                return;
            }

            String[] tokens = line.trim().split(" ");
            if (tokens.length != 3) {
                writeError(outputStream, 400, "Bad Request", "Invalid request line");
                return;
            }

            String target = tokens[1].trim();

            if (target.equals("/")) {
                String responseBody = "Hello world!";
                writeResponse(outputStream, responseBody.getBytes());
                return;
            }

            if (target.startsWith("/")) {
                target = target.substring(1);
            }
            if (!target.startsWith("static/")) {
                target = "static/" + target;
            }
            Path absolutePath = Path.of(
                    Objects.requireNonNull(getClass().getClassLoader().getResource(target)).getPath());
            byte[] responseBodyBytes = Files.readAllBytes(absolutePath);
            writeResponse(outputStream, responseBodyBytes);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final byte[] bytes
    ) throws IOException {
        String response = "HTTP/1.1 200 OK " + "\r\n"
                + "Content-Type: text/html;charset=utf-8 " + "\r\n"
                + "Content-Length: " + bytes.length + " " + "\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void writeError(
            final OutputStream outputStream,
            final int code,
            final String phase,
            final String message
    ) throws IOException {
        String response = "HTTP/1.1 " + code + " " + phase + "\r\n"
                + "Content-Type: text/html;charset=utf-8 " + "\r\n"
                + "Content-Length: " + message.getBytes().length + " " + "\r\n"
                + "Connection: close " + "\r\n"
                + "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(message.getBytes());
        outputStream.flush();
    }
}
