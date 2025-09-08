package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.session.Session;
import com.techcourse.web.session.SessionManager;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Processor;

@Slf4j
public class Http11Processor implements Runnable, Processor {

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = SessionManager.getInstance();
    }

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

        readHeader(reader, requestBuilder);
        readBody(requestBuilder, reader);

        return HttpRequest.from(requestBuilder.toString());
    }

    private void readHeader(final BufferedReader reader, final StringBuilder requestBuilder) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            requestBuilder.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }
    }

    private void readBody(final StringBuilder requestBuilder, final BufferedReader reader) throws IOException {
        final String header = requestBuilder.toString();
        final int contentLength = extractContentLength(header);

        if (contentLength <= 0) {
            return;
        }

        final char[] buffer = new char[contentLength];
        final int eof = -1;
        int totalRead = 0;

        while (totalRead < contentLength) {
            final int bytesRead = reader.read(buffer, totalRead, contentLength - totalRead);
            if (bytesRead == eof) {
                throw new IOException(
                        "Content-Length와 실제 데이터 길이 불일치: 예상 " + contentLength + "바이트, 실제 " + totalRead + "바이트");
            }
            totalRead += bytesRead;
        }
        requestBuilder.append(buffer);
    }

    private int extractContentLength(final String request) {
        for (final String line : request.split("\r\n")) {
            final String contentLengthPrefix = "content-length:";

            if (line.toLowerCase().startsWith(contentLengthPrefix)) {
                return Integer.parseInt(line.substring(contentLengthPrefix.length()).trim());
            }
        }
        return 0;
    }

    private HttpResponse buildResponse(final HttpRequest request) {
        final String path = request.getPath();

        if (HttpMethod.GET == request.getMethod()) {
            if ("/".equals(path)) {
                return new HttpResponse(request.getVersion(), HttpStatus.OK, ContentType.HTML, "Hello world!");
            }

            return serveStaticFile(request, path);
        }

        if (HttpMethod.POST == request.getMethod()) {
            switch (path) {
                case "/login":
                    return handleLoginRequest(request);
                case "/register":
                    return handleSignupRequest(request);
            }
        }

        throw new UnsupportedOperationException();
    }

    private HttpResponse serveStaticFile(final HttpRequest request, final String path) {
        final ContentType contentType = ContentType.from(path);

        if (path.startsWith("/login") && isValidSessionInCookie(request)) {
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        }

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
            return new HttpResponse(request.getVersion(), HttpStatus.INTERNAL_SERVER_ERROR, ContentType.HTML,
                    "Internal Server Error");
        }
    }

    private HttpResponse handleLoginRequest(final HttpRequest request) {
        try {
            final User user = processLogin(request);

            final HttpResponse response = HttpResponse.redirect(request.getVersion(), "/index.html");

            if (isValidSessionInCookie(request)) {
                return response;
            }

            final Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.setCookie("JSESSIONID", session.getId());
            return response;
        } catch (final Exception e) {
            return HttpResponse.redirect(request.getVersion(), "/401.html");
        }
    }

    private boolean isValidSessionInCookie(final HttpRequest request) {
        return sessionManager.isValidSession(
                request.getCookie("JSESSIONID"));
    }

    private User processLogin(final HttpRequest request) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

        user.checkPassword(password);
        return user;
    }

    private HttpResponse handleSignupRequest(final HttpRequest request) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");
        final String email = request.getBodyParam("email");

        try {
            final User user = InMemoryUserRepository.save(User.withoutId(account, password, email));

            log.debug("회원 가입 성공: {}", user);
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        } catch (final Exception e) {
            log.debug("회원 가입 실패: {}", e.getMessage());
            return HttpResponse.redirect(request.getVersion(), "/401.html");
        }
    }
}
