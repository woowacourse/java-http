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
            HttpResponse response = handleRequest(request);

            writeResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws IOException {
        if (request.isMatched(HttpMethod.GET, "/login")) {
            return handleLogin(request);
        }

        if (request.isMatched(HttpMethod.GET, "/")) {
            return handleRoot();
        }

        if (request.isGet() && isStaticResource(request.getPath())) {
            return createStaticResourceResponse(request.getPath());
        }

        return createNotFoundResponse();
    }

    private HttpResponse handleLogin(HttpRequest request) throws IOException {
        User user = authenticate(request);
        log.info(user.toString());

        return createStaticResourceResponse("/login.html");
    }

    private User authenticate(HttpRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.getParamValue("account"))
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));

        if (!user.checkPassword(request.getParamValue("password"))) {
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        return user;
    }

    private HttpResponse createStaticResourceResponse(String path) throws IOException {
        String resourcePath = "static" + path;

        byte[] body = getResourceFileBytes(resourcePath);
        String contentType = resolveContentType(path);

        return HttpResponse.ok(contentType, body);
    }

    private HttpResponse handleRoot() {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);

        return HttpResponse.ok(
                "text/html;charset=utf-8",
                body
        );
    }

    private HttpResponse createNotFoundResponse() throws IOException {
        byte[] body = getResourceFileBytes("static/404.html");
        return HttpResponse.notFound(body);
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

    private boolean isStaticResource(String path) {
        return path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js");
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
