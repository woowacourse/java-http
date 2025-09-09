package org.apache.coyote.http;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.session.Session;
import com.techcourse.web.session.SessionManager;
import java.io.BufferedInputStream;
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
            final HttpResponse response = buildResponse(request);

            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (final Exception e) {
            log.error("요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private HttpRequest buildRequest(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(bufferedInputStream, StandardCharsets.ISO_8859_1));

        final HttpRequestHeader requestHeader = readRequestHeader(reader);
        final HttpRequestBody requestBody = readRequestBody(bufferedInputStream, requestHeader);

        return HttpRequest.from(requestHeader, requestBody);
    }

    private HttpRequestHeader readRequestHeader(final BufferedReader reader) throws IOException {
        final StringBuilder headerBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            headerBuilder.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }

        return HttpRequestHeader.from(headerBuilder.toString());
    }

    private HttpRequestBody readRequestBody(final InputStream inputStream, final HttpRequestHeader header)
            throws IOException {
        final int contentLength = header.getContentLength();
        final ContentType contentType = header.getContentType();

        if (contentLength <= 0) {
            return HttpRequestBody.from("", contentType);
        }

        final byte[] bodyBytes = inputStream.readNBytes(contentLength);
        if (bodyBytes.length != contentLength) {
            throw new IOException(
                    "Content-Length와 실제 데이터 길이 불일치: 예상=" + contentLength + ", 실제=" + bodyBytes.length);
        }

        final String bodyString = new String(bodyBytes, contentType.getDefaultCharset()); // TODO client request charset

        return HttpRequestBody.from(bodyString, contentType);
    }


    private HttpResponse buildResponse(final HttpRequest request) {
        final String path = request.getPath();
        final HttpMethod method = request.getMethod();

        return switch (method) {
            case GET -> handleGetRequest(request, path);
            case POST -> handlePostRequest(request, path);
            default -> throw new UnsupportedOperationException("지원하지 않는 HTTP 메서드: " + method);
        };
    }

    private HttpResponse handleGetRequest(final HttpRequest request, final String path) {
        if ("/".equals(path)) {
            return new HttpResponse(request.getVersion(), HttpStatus.OK, ContentType.HTML, "Hello world!");
        }
        return serveStaticFile(request, path);
    }

    private HttpResponse handlePostRequest(final HttpRequest request, final String path) {
        return switch (path) {
            case "/login" -> handleLoginRequest(request);
            case "/register" -> handleSignupRequest(request);
            default -> throw new UnsupportedOperationException("지원하지 않는 POST 경로: " + path);
        };
    }

    private HttpResponse serveStaticFile(final HttpRequest request, final String path) {
        if (shouldBypassLoginPage(request, path)) {
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        }

        final String resourcePath = buildResourcePath(path);
        return loadAndServeFile(request, resourcePath);
    }

    private boolean shouldBypassLoginPage(final HttpRequest request, final String path) {
        return path.startsWith("/login") && isValidSessionInCookie(request);
    }

    private String buildResourcePath(final String path) {
        final String resourcePath = "static" + path;
        if (path.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ContentType.HTML_EXTENSION;
    }

    private HttpResponse loadAndServeFile(final HttpRequest request, final String resourcePath) {
        try {
            final URL resource = getClass().getClassLoader().getResource(resourcePath);

            if (resource == null) {
                return createNotFoundResponse(request);
            }

            final String fileContent = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
            final ContentType contentType = ContentType.from(resourcePath);

            return new HttpResponse(request.getVersion(), HttpStatus.OK, contentType, fileContent);
        } catch (final Exception e) {
            log.error("정적 파일 서빙 중 오류 발생: {}", resourcePath, e);
            return createServerErrorResponse(request);
        }
    }

    private HttpResponse createNotFoundResponse(final HttpRequest request) {
        final HttpStatus notFound = HttpStatus.NOT_FOUND;
        return new HttpResponse(
                request.getVersion(),
                notFound,
                ContentType.HTML,
                notFound.getReasonPhrase());
    }

    private HttpResponse createServerErrorResponse(final HttpRequest request) {
        final HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new HttpResponse(
                request.getVersion(),
                internalServerError,
                ContentType.HTML,
                internalServerError.getReasonPhrase());
    }

    private HttpResponse handleLoginRequest(final HttpRequest request) {
        try {
            final User user = processLogin(request);
            return createLoginSuccessResponse(request, user);
        } catch (final Exception e) {
            log.debug("로그인 실패: {}", e.getMessage());
            return HttpResponse.redirect(request.getVersion(), "/401.html");
        }
    }

    private HttpResponse createLoginSuccessResponse(final HttpRequest request, final User user) {
        final HttpResponse response = HttpResponse.redirect(request.getVersion(), "/index.html");

        if (isValidSessionInCookie(request)) {
            return response;
        }

        createAndSetSession(response, user);
        return response;
    }

    private void createAndSetSession(final HttpResponse response, final User user) {
        final Session session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);
        response.setCookie("JSESSIONID", session.getId());
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
        try {
            final User user = createUser(request);

            log.debug("회원 가입 성공: {}", user);
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        } catch (final Exception e) {
            log.debug("회원 가입 실패: {}", e.getMessage());
            return HttpResponse.redirect(request.getVersion(), "/401.html");
        }
    }

    private User createUser(final HttpRequest request) {
        final String account = request.getBodyParam("account");
        final String password = request.getBodyParam("password");
        final String email = request.getBodyParam("email");
        return InMemoryUserRepository.save(
                User.withoutId(account, password, email));
    }
}
