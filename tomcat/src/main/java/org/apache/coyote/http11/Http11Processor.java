package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
             final var outputStream = connection.getOutputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var requestLine = RequestLine.from(reader.readLine());
            final var requestPath = requestLine.getPath();
            final Map<String, String> queryParameters = requestLine.getQueryParameters();
            if(requestPath.equals("login")) {
                printMemberInfo(queryParameters.get("account"), queryParameters.get("password"));
            }

            var responseBody = "Hello world!";
            if (!requestPath.isBlank()) {
                var resource = getResourceFrom(requestPath);
                if (resource != null) {
                    var path = Paths.get(resource.toURI());
                    responseBody = Files.readString(path);
                }
            }

            var mimeType = "text/html";
            if (requestPath.endsWith(".css")) {
                mimeType = "text/css";
            }

            final var response = buildHttpResponse(mimeType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void printMemberInfo(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if(optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        if(user.checkPassword(password)) {
            System.out.println("user: " + user);
        }
    }

    private URL getResourceFrom(String requestPath) {
        var resource = getClass().getClassLoader().getResource("static/" + requestPath);
        if(resource == null) {
            resource = getClass().getClassLoader().getResource("static/" + requestPath + ".html");
        }
        return resource;
    }

    private String buildHttpResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
