package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try (BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {

            var responseBody = "Hello world!";

            StringBuilder request = new StringBuilder();

            String line;
            while (!(line = bufferedReader.readLine()).equals("")) {
                request.append(line).append("\r\n");
            }

            String requestToString = request.toString();
            String[] parts = requestToString.split(" ");
            String path = parts[1];

            if (path.startsWith("/login")) {
                String uri = path;
                int index = path.indexOf("?");
                path = uri.substring(0, index) + ".html";

                String queryString = uri.substring(index + 1);
                int queryIndex = queryString.indexOf("&");
                String name = queryString.substring(8, queryIndex);
                String password = queryString.substring(queryIndex + 10);

                if (InMemoryUserRepository.findByAccount(name) != null) {
                    log.info("user: {}", InMemoryUserRepository.findByAccount(name));
                }
            }

            if (!("/").equals(path)) {
                log.info("path: {}", path);
                Path filePath = Path.of(getClass().getResource("/static" + path).toURI());
                responseBody = Files.readString(filePath);
            }



            String contentType = "text/html;charset=utf-8 ";
            if (requestToString.contains("text/css")) {
                contentType = "text/css;charset=utf-8";
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
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
