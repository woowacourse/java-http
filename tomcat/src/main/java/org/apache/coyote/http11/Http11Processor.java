package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final String line = bufferedReader.readLine();

            final String[] httpRequestLine = line.split(" ");
            final String method = httpRequestLine[0];
            final String requestURL = httpRequestLine[1];

            String contentType = "text/html;charset=utf-8 ";
            String responseBody = "";

            if (requestURL.endsWith(".css")) {
                contentType = "text/css";
            }

            if (method.equals("GET") && !requestURL.equals("/")) {
                URL resource = getClass().getResource("/static" + requestURL);

                if (resource == null) {
                    responseBody = "404 Not Found";
                    contentType = "text/plain";
                    resource = getClass().getResource("/static/404.html");
                }

                final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
                responseBody = new String(fileBytes);
            }

            if (method.equals("GET") && requestURL.equals("/")) {
                responseBody = "Hello world!";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
