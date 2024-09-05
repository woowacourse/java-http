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
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Session session;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.session = Session.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

            if (httpRequest.isMethod("POST") && httpRequest.isPath("/register")) {
                String requestBody = httpRequest.getHttpRequestBody().getBody();
                String[] token = requestBody.split("&");
                String account = token[0].split("=")[1];
                String email = token[1].split("=")[1];
                String password = token[2].split("=")[1];
                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);
            }

            var responseBody = "";
            var contentType = "text/html;charset=utf-8 \r\n";

            if (httpRequest.isPath("/")) {
                responseBody = "Hello world!";
            } else if (httpRequest.isMethod("GET")) {
                String fileName = "static" + httpRequest.getPath();
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

            if (httpRequest.getPath().endsWith(".css")) {
                contentType = "text/css;charset=utf-8 \r\n";
            } else if (httpRequest.getPath().endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8 \r\n";
            } else if (httpRequest.getPath().endsWith(".html")) {
                contentType = "text/html;charset=utf-8 \r\n";
            } else if (httpRequest.getPath().endsWith(".png")) {
                contentType = "image/png \r\n";
            } else if (httpRequest.getPath().endsWith(".jpg")) {
                contentType = "image/jpeg \r\n";
            }

            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType +
                            "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
            if (httpRequest.isMethod("POST") && httpRequest.isPath("/login")) {
                String requestBody = httpRequest.getBody();
                String[] token = requestBody.split("&");
                String account = token[0].split("=")[1];
                String password = token[1].split("=")[1];

                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow();
                UUID uuid = UUID.randomUUID();
                if (user.checkPassword(password)) {
                    session.save(uuid.toString(), user);
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Set-Cookie: JSESSIONID=" + uuid,
                            "Location: " + "/index.html");
                    log.info(user.toString());
                } else {
                    response = String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: " + "/401.html");
                    log.error("비밀번호 불일치");
                }
            }
            if (httpRequest.isMethod("POST") && httpRequest.isPath("/register")) {
                response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: " + "/index.html");
            }
            if (httpRequest.isMethod("GET") && httpRequest.isPath("/login.html")) {
                if (httpRequest.containsKey("Cookie")) {
                    String[] cookies = httpRequest.getValue("Cookie").split("; ");
                    String cookie = "";
                    for (String c : cookies) {
                        if (c.contains("JSESSIONID")) {
                            cookie = c.split("=")[1];
                        }
                    }
                    if (session.containsUser(cookie)) {
                        User user = session.getUser(cookie);
                        response = String.join("\r\n",
                                "HTTP/1.1 302 Found ",
                                "Set-Cookie: JSESSIONID=" + cookie,
                                "Location: " + "/index.html");
                        log.info(user.toString());
                    }
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
