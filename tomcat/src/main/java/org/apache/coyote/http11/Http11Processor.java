package org.apache.coyote.http11;

import static com.techcourse.db.InMemoryUserRepository.findByAccount;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private void response(String responseBody, OutputStream outputStream, StatusCode statusCode, String type) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 "+ statusCode,
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void empty(OutputStream outputStream, String contentType) throws IOException {
        final var responseBody = "Hello world!";
        response(responseBody, outputStream, StatusCode.OK, contentType);
    }

    private void login(OutputStream outputStream, Request request) throws IOException {
        String account = request.getRequestParam("account");
        String password = request.getRequestParam("password");
        User user = findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            log.info(user.toString());
            loginSuccess(outputStream, request);
        }
        if ((user != null && !user.checkPassword(password))) {
            loginFail(outputStream, request);
        }
        handling(outputStream, request, StatusCode.OK);
    }

    private void loginFail(OutputStream outputStream, Request request) throws IOException {
        Request unauthorized = Request.changePath(request, "/401");
        handling(outputStream, unauthorized, StatusCode.UNAUTHORIZED);
    }

    private void loginSuccess(OutputStream outputStream, Request request) throws IOException {
        Request found = Request.changePath(request, "/index");
        handling(outputStream, found, StatusCode.FOUND);
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
            Request request = HttpParser.getRequest(inputStream);
            log.info("request: {}", request);
            if (request.getPath().equals("/")) {
                empty(outputStream, request.getContentType());
                return;
            }
            if (request.getPath().startsWith("/login")) {
                login(outputStream, request);
                return;
            }
            handling(outputStream, request, StatusCode.OK);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handling(OutputStream outputStream, Request request, StatusCode statusCode) throws IOException {
        String path = request.getPath();
        String contentType = request.getContentType();
        if (!path.contains(".")) {
            path += "." + contentType;
        }
        log.info("path: {}", path);
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        response(responseBody, outputStream, statusCode, contentType);
    }
}
