package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            StringBuilder requestBuilder = new StringBuilder();
            String line;
            java.io.BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                requestBuilder.append(line).append("\r\n");
            }
            
            String request = requestBuilder.toString();
            String header = request.split("\r\n")[0];
            String[] words = header.split(" ");
            log.info(header);
            URL url = getClass().getClassLoader().getResource("static" + words[1]);
            if (url == null) {
                log.info("Resource not found!");
                return;
            }
            Path path = Paths.get(url.toURI());
            List<String> strings = Files.readAllLines(path);

            StringBuilder sb = new StringBuilder();
            for(var str : strings) {
                sb.append(str + "\n");
            }

            final var responseBody = sb.toString();

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
