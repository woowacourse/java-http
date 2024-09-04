package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void process(final Socket connection) throws UncheckedServletException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            List<String> header = new ArrayList<>();
            for (String data = bufferedReader.readLine(); data != null; data = bufferedReader.readLine()) {
                if (data.isBlank()) {
                    break;
                }
                header.add(data);
            }
            if (header.isEmpty()) {
                return;
            }

            final String requestUri = header.getFirst();
            final String[] elements = requestUri.split(" ");
            final String endpoint = elements[1];

            String responseBody = "";
            if (endpoint.equals("/")) {
                responseBody = "Hello world!";
            } else {
                final URL resource = Http11Processor.class.getResource("/static" + endpoint);
                final Path path = Paths.get(resource.toURI()).toFile().toPath();

                responseBody = Files.readString(path);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();

            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
