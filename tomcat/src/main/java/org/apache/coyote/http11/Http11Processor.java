package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private String requestPath;
    private int statusCode = 200;
    private String statusMessage = "OK";

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String input = bufferedReader.readLine();
            if (input == null) {
                return;
            }
            String requestUri = input.split(" ")[1];
            requestPath = requestUri;
            int index = requestUri.indexOf("?");
            if (index >= 0 && requestUri.substring(0, index).equals("/login")) {
                requestPath = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);
                String[] queries = queryString.split("&");
                String[] account = queries[0].split("=");
                String[] password = queries[1].split("=");
                login(account[1], password[1]);
            }


            String contentType = "text/html;";
            if (requestUri.endsWith(".css")) {
                contentType = "text/css;";
            }
            if (requestUri.endsWith(".js")) {
                contentType = "application/javascript;";
            }

            String resourceName = "static" + requestPath;
            if (contentType.equals("text/html;") && !resourceName.endsWith(".html")) {
                resourceName += ".html";
            }
            URL resource = getClass().getClassLoader().getResource(resourceName);
            Path path = new File(resource.getPath()).toPath();
            byte[] bytes = Files.readAllBytes(path);

            final var responseBody = new String(bytes);
            String response = String.join("\r\n",
                    "HTTP/1.1" + " " + statusCode + " " + statusMessage + " ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        if (!user.checkPassword(password)) {
            requestPath = "/401";
            statusCode = 401;
            statusMessage = "Unauthorized";
            return;
        }
        requestPath = "/index.html";
        statusCode = 302;
        statusMessage = "Found";
        log.info("user : {}", user);
    }
}
