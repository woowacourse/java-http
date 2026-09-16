package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.net.URL;
import java.nio.file.Files;
import java.util.StringTokenizer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = reader.readLine();
            StringTokenizer streamTokenizer = new StringTokenizer(firstLine);
            String method = streamTokenizer.nextToken();
            log.info("method: {}", method);
            String path = streamTokenizer.nextToken();
            log.info("path: {}", path);

            String html = "html";
            if (path.equals("/")) {
                log.info("path is empty");
                final var responseBody = "Hello world!";
                response(responseBody, outputStream, html);
                return;
            }

            if (path.startsWith("/css")) {
                String css = "css";
                log.info(css);
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                response(responseBody, outputStream, css);
            }

            final URL resource = getClass().getClassLoader().getResource("static" + path);
            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            response(responseBody, outputStream, html);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void response(String responseBody, OutputStream outputStream, String type) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
