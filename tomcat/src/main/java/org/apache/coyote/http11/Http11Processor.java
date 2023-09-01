package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int PATH_MESSAGE_INDEX = 1;

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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String startLine = br.readLine();
            String[] startLineElements = startLine.split(" ");
            String path = startLineElements[PATH_MESSAGE_INDEX];

            String response = "";
            if (path.equals("/")) {
                final String responseBody = "Hello world!";

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else if (path.endsWith(".html")) {
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else if (path.endsWith(".css")) {
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else if (path.endsWith(".js")) {
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/javascript;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            } else if (path.startsWith("/login")) {
                final URL resource = getClass().getClassLoader().getResource("static/login.html");
                String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                int index = path.indexOf("?");
                String params = path.substring(index + 1);
                String[] elements = params.split("&");
                String[] accountPair = elements[0].split("=");
                String[] passwordPair = elements[1].split("=");
                String account = accountPair[1];
                String password = passwordPair[1];

                User user = InMemoryUserRepository.findByAccount(account).get();
                if (user.checkPassword(password)) {
                    log.info("user : {}", user);
                }
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
