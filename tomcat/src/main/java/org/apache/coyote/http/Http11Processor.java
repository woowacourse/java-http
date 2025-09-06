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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        log.info("연결된 호스트: {}, 포트: {}", connection.getInetAddress(), connection.getPort());
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
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private HttpRequest buildRequest(final InputStream inputStream) throws IOException {
        final StringBuilder requestBuilder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        while ((line = reader.readLine()) != null) {
            requestBuilder.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }

        return HttpRequest.from(requestBuilder.toString());
    }

    private HttpResponse buildResponse(final HttpRequest request) {
        final String path = request.getPath();

        if ("/".equals(path)) {
            return new HttpResponse(request.getVersion(), HttpStatus.OK, ContentType.HTML, "Hello world!");
        }

        if ("/login".equals(path) && !request.getQueryParams().isEmpty()) {
            return handleLoginRequest(request);
        }

        return serveStaticFile(request, path);
    }

    private HttpResponse handleLoginRequest(final HttpRequest request) {
        final boolean loginSuccess = processLogin(request);

        if (loginSuccess) {
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        }
        return HttpResponse.redirect(request.getVersion(), "/401.html");
    }

    private boolean processLogin(final HttpRequest request) {
        final String account = request.getQueryParam("account");
        final String password = request.getQueryParam("password");

        if (account.isEmpty() || password.isEmpty()) {
            return false;
        }

        try {
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

            return user.checkPassword(password);
        } catch (final Exception e) {
            return false;
        }
    }

    private HttpResponse serveStaticFile(final HttpRequest request, final String path) {
        final ContentType contentType = ContentType.from(path);
        String resourcePath = "static" + path;

        if (!path.contains(".")) {
            resourcePath += ".html";
        }

        try {
            final URL resource = getClass().getClassLoader().getResource(resourcePath);

            if (resource == null) {
                return new HttpResponse(request.getVersion(), HttpStatus.NOT_FOUND, ContentType.HTML, "Not Found");
            }

            final String fileContent = new String(
                    Files.readAllBytes(Paths.get(resource.toURI())), StandardCharsets.UTF_8);

            return new HttpResponse(request.getVersion(), HttpStatus.OK, contentType, fileContent);

        } catch (final Exception e) {
            log.error("정적 파일 서빙 중 오류 발생: {}", resourcePath, e);
            return new HttpResponse(request.getVersion(), HttpStatus.INTERNAL_SERVER_ERROR, ContentType.HTML, "Internal Server Error");
        }
    }
}
