package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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

    private final Http11ResourceFinder resourceFinder;

    private final Http11ContentTypeFinder contentTypeFinder;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceFinder = new Http11ResourceFinder();
        this.contentTypeFinder = new Http11ContentTypeFinder();
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
        Http11Request request = Http11Request.from(inputStream);

        String requestURI = request.requestUri();

        Path path = resourceFinder.find(requestURI);

        if (path.getFileName().toString().equals("login.html")) {
            login(request, outputStream);
        }
        if (path.getFileName().toString().equals("register.html")) {
            register(request, outputStream);
        }

        sendStaticResourceResponse(path, outputStream);
    }

    private void login(Http11Request request, OutputStream outputStream) {
        Http11Method http11Method = request.method();
        if (http11Method.equals(Http11Method.GET)) {
            return;
        }
        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");

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

    private void register(Http11Request request, OutputStream outputStream) {
        Http11Method http11Method = request.method();
        if (http11Method.equals(Http11Method.GET)) {
            return;
        }

        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");
        String email = requestBody.getOrDefault("email", "");

        InMemoryUserRepository.save(new User(account, password, email));

        sendRedirect("/index.html", outputStream);
    }

    private void sendStaticResourceResponse(Path path, OutputStream outputStream) throws IOException {
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
}
