package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }

            Map<String, String> httpRequestHeaders = new HashMap<>();

            while (!firstLine.isEmpty()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                String[] headerParts = line.split(": ");
                httpRequestHeaders.put(headerParts[0], headerParts[1]);
            }

            String httpMethod = firstLine.split(" ")[0];
            String page = firstLine.split(" ")[1];

            String responseBody = "";
            String response = "";
            if (page.equals("/")) {
                responseBody = "Hello world!";
                response = generate200Response(responseBody, "text/html");
            } else if (page.startsWith("/login?")) {
                int index = page.indexOf("?");
                String queryString = page.substring(index + 1);
                String account = queryString.split("&")[0].split("=")[1];
                String password = queryString.split("&")[1].split("=")[1];
                User user = InMemoryUserRepository.findByAccount(account).get();

                if (user.checkPassword(password)) {
                    log.info("user : {}", user);
                    URL url = getClass().getClassLoader().getResource("static/index.html");
                    if (url == null) {
                        return;
                    }

                    Path path = Path.of(url.toURI());
                    responseBody = new String(Files.readAllBytes(path));
                    response = generate302Response(responseBody, "text/html", "/index.html");
                } else {
                    URL url = getClass().getClassLoader().getResource("static/401.html");
                    if (url == null) {
                        return;
                    }

                    Path path = Path.of(url.toURI());
                    responseBody = new String(Files.readAllBytes(path));
                    response = generate302Response(responseBody, "text/html", "/401.html");
                }
            } else if (page.equals("/register") && httpMethod.equals("POST")) {
                int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                String account = requestBody.split("&")[0].split("=")[1];
                String email = requestBody.split("&")[1].split("=")[1];
                String password = requestBody.split("&")[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, email, password));

                URL url = getClass().getClassLoader().getResource("static/index.html");
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generate302Response(responseBody, "text/html", "/index.html");
            } else if (page.startsWith("/css/")) {
                URL url = getClass().getClassLoader().getResource("static" + page);
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generate200Response(responseBody, "text/css");
            } else if (page.contains(".js")) {
                URL url = getClass().getClassLoader().getResource("static" + page);
                if (url == null) {
                    return;
                }
                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generate200Response(responseBody, "text/javascript");
            } else if (page.endsWith(".html")) {
                URL url = getClass().getClassLoader().getResource("static" + page);
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generate200Response(responseBody, "text/html");
            } else {
                URL url = getClass().getClassLoader().getResource("static" + page + ".html");
                if (url == null) {
                    return;
                }

                Path path = Path.of(url.toURI());
                responseBody = new String(Files.readAllBytes(path));
                response = generate200Response(responseBody, "text/html");
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String generate200Response(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String generate302Response(String responseBody, String contentType, String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + redirectUrl,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + responseBody.getBytes().length,
                "",
                responseBody);
    }

}
