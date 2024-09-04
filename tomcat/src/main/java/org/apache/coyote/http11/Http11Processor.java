package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            System.out.println(line);
            if (line == null || line.isEmpty()) {
                return;
            }

            String[] requestLine = line.split(" ");
            if (requestLine.length < 3) {
                return;
            }
            String uri = requestLine[1];
            String path = uri;
            String queryString = "";
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            var responseBody = "";
            var contentType = "text/html;charset=utf-8 \r\n";
            if (!path.equals("/") && !path.contains(".")) {
                path += ".html";
            }

            if (path.equals("/")) {
                responseBody = "Hello world!";
            } else {
                String fileName = "static" + path;
                var resourceUrl = getClass().getClassLoader().getResource(fileName);
                if (resourceUrl == null) {
                    final var response = String.join("\r\n",
                            "HTTP/1.1 404 Not Found",
                            "Content-Type: text/html;charset=utf-8 \r\n",
                            "",
                            "<h1>404 Not Found</h1>");
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
                Path filePath = Path.of(resourceUrl.toURI());
                responseBody = new String(Files.readAllBytes(filePath));
            }

            if (path.endsWith(".css")) {
                contentType = "text/css;charset=utf-8 \r\n";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8 \r\n";
            } else if (path.endsWith(".html")) {
                contentType = "text/html;charset=utf-8 \r\n";
            } else if (path.endsWith(".png")) {
                contentType = "image/png \r\n";
            } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                contentType = "image/jpeg \r\n";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType +
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            if (!queryString.isEmpty()) {
                String[] query = queryString.split("&");
                String account = query[0].split("=")[1];
                String password = query[1].split("=")[1];
                System.out.println(account + " " + password);
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow();
                if (user.checkPassword(password)) {
                    log.info(user.toString());
                } else {
                    log.error("비밀번호 불일치");
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
