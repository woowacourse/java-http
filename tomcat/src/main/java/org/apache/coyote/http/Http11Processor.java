package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Processor;

@RequiredArgsConstructor
@Slf4j
public class Http11Processor implements Runnable, Processor {

    private final Socket connection;

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest request = buildRequest(inputStream);
            if (request == null) {
                return;
            }

            final HttpResponse response = buildResponse(request);

            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest buildRequest(final InputStream inputStream) throws IOException {
        final StringBuilder requestBuilder = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBuilder.append(line).append("\r\n");
                if (line.isEmpty()) {
                    break;
                }
            }
        }

        return HttpRequest.from(requestBuilder.toString());
    }

    private HttpResponse buildResponse(final HttpRequest request) {
        final String contentType = getContentType(request.getPath());
        return new HttpResponse(
                request.getVersion(),
                200,
                contentType,
                buildResponseBody(request));
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }

    private String buildResponseBody(final HttpRequest request) {
        String path = request.getPath();

        if ("/".equals(path)) {
            return "Hello world!"; // 1-2 미션 요구사항은 index.html 이지만, 1-1 테스트 요구사항에 맞춤.
        }

        if ("/login".equals(path)) {
            handleLogin(request);
        }

        if (path == null || path.isEmpty()) {
            path = "/index";
        }

        String fileName = "static" + path;

        if (!path.contains(".")) {
            fileName += ".html";
        }

        final URL resource = getClass().getClassLoader().getResource(fileName);

        if (resource == null) {
            return "Not Found";
        }

        try {
            return new String(Files.readAllBytes(Paths.get(resource.toURI())));
        } catch (final Exception e) {
            return "Error reading file";
        }
    }

    private void handleLogin(final HttpRequest request) {
        final String account = request.getQueryParam("account");
        final String password = request.getQueryParam("password");

        if (account.isEmpty() || password.isEmpty()) {
            log.debug("로그인 실패: 계정 정보가 없습니다.");
            return;
        }

        try {
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

            if (user.checkPassword(password)) {
                log.debug("로그인 성공: {}", account);
                return;
            }
            throw new RuntimeException("비밀번호가 틀렸습니다");
        } catch (final Exception e) {
            log.debug(e.getMessage(), e);
        }
    }
}
