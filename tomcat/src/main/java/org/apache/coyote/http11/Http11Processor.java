package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        try (connection) {
            sendResponse(connection);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendResponse(Socket connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = connection.getOutputStream();
        String requestMessage = requestParser.readAsString(inputStream);

        String requestURI = requestParser.parseRequestURI(requestMessage);

        Path path = resourceFinder.find(requestURI);

        Http11Method http11Method = requestParser.parseMethod(requestMessage);

        if (path.getFileName().toString().equals("login.html") && http11Method.equals(Http11Method.GET)) {
            login(requestURI, outputStream);
            return;
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
    }

    private void login(String requestURI, OutputStream outputStream) {
        LinkedHashMap<String, String> queryStrings = queryStringParser.parse(requestURI);
        String account = queryStrings.getOrDefault("account", "");
        String password = queryStrings.getOrDefault("password", "");

        boolean loginSuccess = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();

        if (loginSuccess) {
            sendRedirect("/index.html", outputStream);
            return;
        }
        sendRedirect("/401.html", outputStream);
    }

    private void sendRedirect(String uri, OutputStream outputStream) {
        String statusLine = "HTTP/1.1 302 Found ";
        String location = "Location: %s".formatted(uri);
        var response = String.join("\r\n", statusLine, location, "");
        try (outputStream) {
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
