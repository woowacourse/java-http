package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
             final var outputStream = connection.getOutputStream()) {

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();

            if (line == null) {
                return;
            }

            var responseBody = "Hello world!";

            String[] tokens = line.split(" ");
            String uri = tokens[1];

            if (!uri.equals("/")) {
                int index = uri.indexOf("?");

                String path = uri;

                if (index != -1) {
                    path = uri.substring(0, index);
                    String queryString = uri.substring(index + 1);

                    Map<String, String> params = new HashMap<>();

                    for (String query : queryString.split("&")) {
                        String[] pair = query.split("=", 2);

                        if (pair.length == 2) {
                            params.put(pair[0], pair[1]);
                        }
                    }

                    InMemoryUserRepository.findByAccount(params.get("account"))
                            .filter(user ->
                                    user.checkPassword(params.get("password")))
                            .ifPresent(user ->
                                    log.info("회원 조회 성공: account={}",
                                            user.getAccount()));
                }

                if ("/login".equals(path)) {
                    path = "/login.html";
                }

                final var resourceFile =
                        getResourceFile("static" + path);

                responseBody = Files.readString(
                        resourceFile.toPath(),
                        StandardCharsets.UTF_8
                );
            }

            var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            if(uri.endsWith(".css")) {
                response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File getResourceFile(String fileName) {
        final URL resource = getClass().getClassLoader().getResource(fileName);

        try {
            return Path.of(resource.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
