package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        try {
            process(connection);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(final Socket connection) throws URISyntaxException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] split = bufferedReader.readLine().split(" ");
            if (split[1].equals("/favicon.ico")) {
                return;
            }

            String[] part = split[1].split("\\?");

            String fileName = part[0];
            if (fileName != null && !fileName.isBlank()) {
                if (!fileName.equals("/") && !fileName.contains(".")) {
                    fileName += ".html";
                }
            }
            fileName = "static" + fileName;

            String[] queries = new String[0];
            if (part.length > 1) {
                queries = part[1].split("&");
            }
            Map<String, String> params = new HashMap<>();
            for (String query : queries) {
                String[] q = query.split("=");
                params.put(q[0], q[1]);
            }

            URL resource = getClass().getClassLoader().getResource(fileName);
            final Path path = Paths.get(Objects.requireNonNull(resource).toURI());

            String response;
            if (Files.isDirectory(path)) {
                final var responseBody = "Hello world!";
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (path.toString().endsWith(".css")) {
                byte[] responseBody = Files.readAllBytes(path);
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        new String(responseBody, StandardCharsets.UTF_8));

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            byte[] responseBody = Files.readAllBytes(path);
            response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody, StandardCharsets.UTF_8));

            if (queries.length > 0) {
                User user = InMemoryUserRepository.findByAccount(params.get("account"))
                        .orElseThrow(IllegalArgumentException::new);
                log.info("user: {}", user);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
