package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String request = bufferedReader.readLine();
            Map<String, String> headers = new HashMap<>();
            String line = bufferedReader.readLine();
            while (line != null && !line.isEmpty()) {
                String[] header = line.split(": ");
                headers.put(header[0], header[1]);
                line = bufferedReader.readLine();
            }
            String[] requestLines = request.split(" ");
            String httpMethod = requestLines[0];
            String uri = requestLines[1];
            String httpVersion = requestLines[2];

            String file = this.getClass().getClassLoader().getResource("static/index.html").getFile();
            File html = new File(file);


            var responseBody = Files.readString(html.toPath());
            if (!uri.equals("/index.html")) {
                responseBody = "Hello world!";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
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
