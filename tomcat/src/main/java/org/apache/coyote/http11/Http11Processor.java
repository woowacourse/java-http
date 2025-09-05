package org.apache.coyote.http11;

import ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String header = reader.readLine();

            if (header == null || header.isBlank()) { return; }

            String[] split = header.split(" ");

            String requestResource = split[1];

            String body = null;

            String response = null;

            System.out.println(header);

            if (requestResource.endsWith(".css")) {
                String resourcePath = "./static" + requestResource;
                URL systemResource = ClassLoader.getSystemResource(resourcePath);
                Path path = Path.of(systemResource.getPath());
                final var responseBody = Files.readAllBytes(path);
                body = new String(responseBody);

                String extension = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
                String contentType = String.join("/","text",extension);
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + body.getBytes().length + " ",
                        "",
                        body);
            }

            if (requestResource.endsWith(".html")) {
                String resourcePath = "./static" + requestResource;
                URL systemResource = ClassLoader.getSystemResource(resourcePath);
                Path path = Path.of(systemResource.getPath());
                final var responseBody = Files.readAllBytes(path);
                body = new String(responseBody);

                String extension = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);

                String contentType = String.join("/","text",extension);

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + body.getBytes().length + " ",
                        "",
                        body);
            }

            if (requestResource.endsWith(".js")) {
                String resourcePath = "./static" + requestResource;
                URL systemResource = ClassLoader.getSystemResource(resourcePath);
                Path path = Path.of(systemResource.getPath());
                final var responseBody = Files.readAllBytes(path);
                body = new String(responseBody);

                String extension = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
                String contentType = String.join("/","text",extension);
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + body.getBytes().length + " ",
                        "",
                        body);
            }

            if (requestResource.startsWith("/login")) {

                int index = requestResource.indexOf("?");
                String resource = requestResource.substring(0, index);
                String queryString = requestResource.substring(index + 1);

                Map<String, String> queryParameters = new HashMap<>();
                String[] parameters = queryString.split("&");
                for (String parameter : parameters) {
                    String key = parameter.split("=")[0];
                    String value = parameter.split("=")[1];
                    queryParameters.put(key, value);
                }


                String resourcePath = "./static" + resource + ".html";
                URL systemResource = ClassLoader.getSystemResource(resourcePath);
                Path path = Path.of(systemResource.getPath());
                final var responseBody = Files.readAllBytes(path);
                body = new String(responseBody);

                String extension = resourcePath.substring(resourcePath.lastIndexOf('.') + 1);
                String contentType = String.join("/","text",extension);
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + ";charset=utf-8 ",
                        "Content-Length: " + body.getBytes().length + " ",
                        "",
                        body);

                User user = InMemoryUserRepository.findByAccount(queryParameters.get("account")).orElse(null);
                if (user != null) {
                    System.out.println(user);
                }
            }



            if ("/".equals(requestResource)) {
                body = "Hello world!";

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + body.getBytes().length + " ",
                        "",
                        body);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
