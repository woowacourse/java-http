package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            String response = handleRequest(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleRequest(HttpRequest httpRequest) throws URISyntaxException, IOException {
        String requestTarget = httpRequest.getRequestTarget();

        if (requestTarget.equals("/")) {
            return createResponse("static/index.html", "text/html", "200 OK");
        }
        if (httpRequest.isPath("/login")) {
            handleLogin(httpRequest);
        }

        String resourcePath = getResourcePath(requestTarget);
        String contentType = getContentType(requestTarget);

        if (ClassLoader.getSystemResource(resourcePath) == null) {
            return createResponse("static/404.html", "text/html", "404 Not Found");
        }

        return createResponse(resourcePath, contentType, "200 OK");
    }

    private String getResourcePath(String requestTarget) {
        String resourcePath = "static" + requestTarget;

        if (requestTarget.equals("/login") || requestTarget.equals("/register")) {
            resourcePath += ".html";
        }

        return resourcePath;
    }

    private String getContentType(String requestTarget){
        if(requestTarget.endsWith(".css")){
            return "text/css";
        }
        if(requestTarget.endsWith(".js")){
            return "text/javascript";
        }
        return "text/html";
    }

    private void handleLogin(HttpRequest httpRequest) {
        String account = httpRequest.getQueryParameter("account");
        String password = httpRequest.getQueryParameter("password");
        if (account == null || password == null) {
            return;
        }

        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private String createResponse(String resourcePath, String contentType, String status) throws URISyntaxException, IOException {
        String responseBody = createResponseBody(resourcePath);
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createResponseBody(String resourcePath) throws URISyntaxException, IOException {
        final Path path = Path.of(ClassLoader.getSystemResource(resourcePath).toURI());
        return Files.readString(path);
    }
}
