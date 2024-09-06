package org.apache.coyote.http11;

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

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String path = bufferedReader.readLine().split(" ")[1];


            path = updatePath(path);

            var responseBody = "Hello world!";

            if(!path.equals("/")) {
                URL resource = getClass().getClassLoader().getResource("static" + path);;
                final Path filePath = new File(resource.getPath()).toPath();
                responseBody = Files.readString(filePath);
            }

            String extension = path.split("\\.")[1];
            String contentType = "text/html";
            if(extension.equals("css")) {
                contentType = "text/css";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String updatePath(String path) {
        String url = path;
        String account = "";
        String password = "";

        if(path.contains("/login")) {
            if(path.contains("?")) {
                int index = path.indexOf("?");
                String queryString = path.substring(index + 1);
                url = path.substring(0, index);
                String[] params = queryString.split("&");
                account = params[0].split("=")[1];
                password = params[1].split("=")[1];
            }
            path += ".html";
            if(!account.isBlank()) {
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
                if(user.checkPassword(password)) {
                    log.info(user.toString());
                }
            }
        }
        return path;
    }
}
