package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.StringTokenizer;
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

            final String response = getResponse(inputStream);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(InputStream requestStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("GET")) {
                final String requestUri = line.split(" ")[1];
                final int index = requestUri.indexOf("?");

                String path = requestUri;
                String queryString = "";
                if (index != -1) {
                    path = requestUri.substring(0, index);
                    queryString = requestUri.substring(index + 1);
                }

                if (path.equals("/")) {
                    return getRootPage();
                }

                if (path.endsWith("css")) {
                    final URL resource = getClass().getClassLoader().getResource("static" + path);
                    final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                    return String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/css;charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);
                }

                if (path.endsWith("svg")) {
                    final URL resource = getClass().getClassLoader().getResource("static" + path);
                    final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                    return String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: image/svg+xml ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);
                }

                if (!path.contains(".")) {
                    path += ".html";
                }
                final URL resource = getClass().getClassLoader().getResource("static" + path);
                final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

                if (path.startsWith("/login") && !queryString.isEmpty()) {
                    StringTokenizer tokenizer = new StringTokenizer(queryString, "&|=");
                    String account = "";
                    String password = "";
                    while (tokenizer.hasMoreTokens()) {
                        if (tokenizer.nextToken().equals("account") && tokenizer.hasMoreTokens()) {
                            account = tokenizer.nextToken();
                        }
                        if (tokenizer.nextToken().equals("password") && tokenizer.hasMoreTokens()) {
                            password = tokenizer.nextToken();
                        }
                    }

                    Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
                    if (loginUser.isPresent()) {
                        final User user = loginUser.get();
                        if (user.checkPassword(password)) {
                            log.info("user : {}", user);
                            return String.join("\r\n",
                                    "HTTP/1.1 302 Found ",
                                    "Location: http://localhost:8080/index.html ",
                                    "Content-Type: text/html;charset=utf-8 ",
                                    "Content-Length: 0 ",
                                    "");
                        }
                    }
                    return String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: http://localhost:8080/404.html ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: 0 ",
                            "");
                }

                return String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
            }
        }
        return null;
    }

    private String getRootPage() {
        final var responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
