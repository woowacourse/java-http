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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
             final var streamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(streamReader);
             final var outputStream = connection.getOutputStream()) {

            String requestLine = bufferedReader.readLine();
            String[] splitRequestLine = requestLine.split(" ");

            String method = splitRequestLine[0];
            String requestTarget = splitRequestLine[1];
            String protocol = splitRequestLine[2];

            if (method.equals("GET")) {
                String contentType = null;
                final byte[] responseBody;

                if (requestTarget.equals("/")) {
                    contentType = "text/html;charset=utf-8 ";
                    responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                } else {
                    int queryStartIndex = requestTarget.indexOf("?");

                    String resourceName;
                    String queryString;

                    if (queryStartIndex >= 0) {
                        resourceName = requestTarget.substring(0, queryStartIndex);
                        queryString = requestTarget.substring(queryStartIndex + 1);
                    } else {
                        resourceName = requestTarget;
                        queryString = "";
                    }

                    Map<String, String> queryParameters = new HashMap<>();

                    if (!queryString.equals("")) {
                        String[] parameters = queryString.split("&");

                        for (String parameter : parameters) {
                            String[] keyValue = parameter.split("=", 2);
                            queryParameters.put(keyValue[0], keyValue[1]);
                        }
                    }

                    if (resourceName.equals("/login")) {
                        String account = queryParameters.get("account");
                        String password = queryParameters.get("password");

                        if (account != null && password != null) {
                            Optional<User> user = InMemoryUserRepository.findByAccount(account);

                            if (user.isPresent() && user.get().checkPassword(password)) {
                                log.info("user = {}", user);
                            }
                        }

                        resourceName = "login.html";
                    }

                    if (resourceName.endsWith(".css")) {
                        contentType = "text/css;charset=utf-8";
                    }
                    else if (resourceName.endsWith(".html")) {
                        contentType = "text/html;charset=utf-8";
                    }

                    log.info(resourceName);
                    final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
                    final Path path = new File(resource.getFile()).toPath();
                    responseBody = Files.readAllBytes(path);
                }

                final var header = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType,
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        "");

                outputStream.write(header.getBytes());
                outputStream.write(responseBody);
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
