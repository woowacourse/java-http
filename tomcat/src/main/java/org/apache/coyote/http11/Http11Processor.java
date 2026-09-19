package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            HttpRequest request = HttpRequest.from(requestLine);

            if (request.isMatched(HttpMethod.GET, "/login")) {
                handleLogin(request);
                writeStaticResource("/login.html", outputStream);
                return;
            }

            if (request.isMatched(HttpMethod.GET, "/")) {
                handleRoot(outputStream);
                return;
            }

            if (request.isGet() && isStaticResource(request.getPath())) {
                writeStaticResource(request.getPath(), outputStream);
                return;
            }

            // 404
            writeNotFoundResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(HttpRequest request) throws IOException {
        User user = findUserOrThrow(request);
        validatePassword(user, request);
        log.info(user.toString());
    }

    private User findUserOrThrow(HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.getParamValue("account"))
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));
    }

    private void validatePassword(User user, HttpRequest request) {
        if (!user.checkPassword(request.getParamValue("password"))) {
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

    private void writeStaticResource(String path, OutputStream outputStream) throws IOException {
        String resourcePath = "static" + path;

        byte[] body = getResourceFileBytes(resourcePath);
        String contentType = resolveContentType(path);

        HttpResponse response = HttpResponse.ok(contentType, body);
        writeResponse(outputStream, response);
    }

    private byte[] getResourceFileBytes(String path) throws IOException {
        try (final var fileStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (fileStream == null) {
                throw new RuntimeException(path + "을 찾을 수 없습니다.");
            }
            return fileStream.readAllBytes();
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "application/octet-stream";
    }

    private void handleRoot(OutputStream outputStream) throws IOException {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        HttpResponse response = HttpResponse.ok("text/html;charset=utf-8", body);
        writeResponse(outputStream, response);
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js");
    }

    private void writeNotFoundResponse(OutputStream outputStream) throws IOException {
        byte[] body = getResourceFileBytes("static/404.html");
        HttpResponse response = HttpResponse.notFound(body);
        writeResponse(outputStream, response);
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
