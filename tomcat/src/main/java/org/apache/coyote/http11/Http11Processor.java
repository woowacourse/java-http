package org.apache.coyote.http11;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final Http11Request request = Http11Request.of(inputStream);
            String url = request.getRequestUrl();
            String responseBody = "Hello world!";
            String contentType = "html";
            if (url == null) {
                throw new IllegalArgumentException("잘못된 형식의 요청입니다.");
            }

            if ("/".equals(url)) {
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/" + contentType + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            String resourcePath = url;
            if ("/login".equals(url.substring(0, 6))) {
                resourcePath = "/login.html";

                int index = url.indexOf("?");
                Map<String, String> queryStrings = new HashMap<>();
                for (String querystring : url.substring(index + 1).split("&")) {
                    String name = querystring.split("=")[0];
                    String value = querystring.split("=")[1];
                    queryStrings.put(name, value);
                }

                Optional<User> user = InMemoryUserRepository.findByAccount(queryStrings.get("account"));

                if (user.isPresent() && user.get().checkPassword(queryStrings.get("password"))) {
                    log.info(queryStrings.get("account") + "는 존재하는 유저입니다.");
                }
            }

            final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
            if (resource == null) {
                throw new FileNotFoundException(resourcePath + " not found");
            }
            responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            contentType = resourcePath.split("\\.")[1];
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
