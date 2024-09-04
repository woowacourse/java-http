package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String STATIC_PATH = "/static";

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            Path path = Path.of("");
            StringBuilder responseBody = new StringBuilder();
            String contentType = "";

            while (bufferedReader.ready()) {  // TODO: 404 추가하기
                String line = bufferedReader.readLine();
                if (line.startsWith("GET")) {
                    String resourceURI = line.split(" ")[1];
                    if (resourceURI.equals("/")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + "/index.html").getPath());
                    }
                    if (resourceURI.endsWith(".html")) {
                        contentType = "text/html; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + resourceURI).getPath());
                    }
                    if (resourceURI.endsWith(".css")) {
                        contentType = "text/css; charset=utf-8 ";
                        path = Path.of(getClass().getResource(STATIC_PATH + resourceURI).getPath());

                    }
                    if (resourceURI.endsWith(".js")) {
                        contentType = "application/javascript ";
                        path = Path.of(getClass().getResource(STATIC_PATH + resourceURI).getPath());

                    }
                }
            }
            Files.readAllLines(path)
                    .stream()
                    .forEach(line -> responseBody.append(line).append("\n"));

            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.toString().getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
