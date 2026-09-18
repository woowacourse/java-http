package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
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

            if (request.isGetMethod() && request.getPath().equals("/index.html")) {
                writeStaticResource("static/index.html", "text/html;charset=utf-8", outputStream);
                return;
            }

            if (request.isGetMethod() && request.getPath().equals("/css/styles.css")) {
                writeStaticResource("static/css/styles.css", "text/css;charset=utf-8", outputStream);
                return;
            }

            if (request.isGetMethod() && request.getPath().equals("/login")) {
                User user = findUserOrThrow(request);
                validatePassword(user, request);

                writeStaticResource("static/login.html", "text/html;charset=utf-8", outputStream);
                log.info(user.toString());
                return;
            }

            byte[] body = "Hello World!".getBytes();
            HttpResponse response = HttpResponse.ok("text/html;charset=utf-8", body);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void validatePassword(User user, HttpRequest request) {
        if (!user.checkPassword(request.getParamValue("password"))) {
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }

    private User findUserOrThrow(HttpRequest request) {
        return InMemoryUserRepository.findByAccount(request.getParamValue("account"))
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));
    }

    private void writeStaticResource(String path, String contentType, OutputStream outputStream) throws IOException {
        byte[] body = getResourceFileBytes(path);
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

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }
}
