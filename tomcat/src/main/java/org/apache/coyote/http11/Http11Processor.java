package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final Http11RequestParser requestParser;

    private final Http11ResourceFinder resourceFinder;

    private final Http11ContentTypeFinder contentTypeFinder;

    private final Http11QueryStringParser queryStringParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestParser = new Http11RequestParser();
        this.resourceFinder = new Http11ResourceFinder();
        this.contentTypeFinder = new Http11ContentTypeFinder();
        this.queryStringParser = new Http11QueryStringParser();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {
            String requestURI = requestParser.parseRequestURI(inputStream);

            Path path = resourceFinder.find(requestURI);

            if (path.getFileName().toString().equals("login.html")) {
                LinkedHashMap<String, String> queryStrings = queryStringParser.parse(requestURI);
                String account = queryStrings.getOrDefault("account", "");
                String password = queryStrings.getOrDefault("password", "");

                InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password))
                        .map(User::toString)
                        .ifPresent(log::info);
            }

            String contentTypes = contentTypeFinder.find(path);

            List<String> fileLines = Files.readAllLines(path);
            var responseBody = String.join("\n", fileLines);

            String statusLine = "HTTP/1.1 200 OK ";
            String contentType = "Content-Type: %s;charset=utf-8 ".formatted(contentTypes);
            String contentLength = "Content-Length: " + responseBody.getBytes().length + " ";
            String empty = "";
            var response = String.join("\r\n",
                    statusLine,
                    contentType,
                    contentLength,
                    empty,
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
