package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpResponse httpResponse = createResponse(inputStream);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (EOFException e) {
            log.info("클라이언트가 요청 없이 연결을 닫았습니다.");
        } catch (IOException e) {
            log.error("연결에 응답하지 못했습니다.", e);
        } catch (RuntimeException e) {
            log.error("응답을 전송하지 못했습니다.", e);
        }
    }

    private HttpResponse createResponse(final InputStream inputStream) throws EOFException {
        try {
            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            log.info("httpRequest = {}", httpRequest);

            return handle(httpRequest);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 요청입니다. {}", e.getMessage());

            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 400, "Bad Request"),
                    Map.of(CONTENT_LENGTH, "0"),
                    new byte[0]
            );
        } catch (EOFException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            log.error("요청을 처리하지 못했습니다.", e);

            return serverError();
        }
    }

    private HttpResponse handle(final HttpRequest request) throws IOException {
        final String uri = request.requestLine().uri();

        if ("/".equals(uri) && request.isGet()) {
            byte[] responseBody = "Hello world!".getBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    headers,
                    responseBody
            );
        }
        if ("/login".equals(uri) && request.isGet()) {
            return getStaticResource("/login.html");
        }
        if ("/login".equals(uri) && request.isPost()) {
            return login(request);
        }
        if ("/register".equals(uri) && request.isGet()) {
            return getStaticResource("/register.html");
        }
        if ("/register".equals(uri) && request.isPost()) {
            return register(request);
        }

        return getStaticResource(uri);
    }

    private HttpResponse getStaticResource(final String uri) throws IOException {
        final URL resource = getClass()
                .getClassLoader().getResource("static" + uri);

        if (resource == null) {
            return readResource(requiredResource("static/404.html"),
                    new HttpStatusLine(HTTP_VERSION, 404, "Not Found"));
        }

        return readResource(resource,
                new HttpStatusLine(HTTP_VERSION, 200, "OK"));
    }

    private URL requiredResource(final String path) {
        return Objects.requireNonNull(
                getClass().getClassLoader().getResource(path),
                path + "이 존재하지 않습니다."
        );
    }

    private HttpResponse readResource(final URL resource, final HttpStatusLine statusLine) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            byte[] responseBody = inputStream.readAllBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, contentTypeOf(resource.getPath()));
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(statusLine, headers, responseBody);
        }
    }

    private String contentTypeOf(String path) {
        String contentType = URLConnection.guessContentTypeFromName(path);

        if (contentType == null) {
            return "application/octet-stream";
        }

        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        return contentType;
    }

    private HttpResponse login(final HttpRequest request) {
        final String account = request.body().get("account");
        final String password = request.body().get("password");

        if (account != null && password != null) {
            boolean isLoggedIn = InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password)).isPresent();

            if (isLoggedIn) {
                return redirectToHomeWithLoggedIn();
            }
        }

        return redirectTo("/401.html");
    }

    private HttpResponse register(HttpRequest request) {
        String account = request.body().get("account");
        String email = request.body().get("email");
        String password = request.body().get("password");

        if (account == null || email == null || password == null) {
            log.info("잘못된 회원가입 요청입니다.");

            return redirectTo("/401.html");
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 가입된 계정입니다: {}", account);

            return redirectTo("/401.html");
        }

        InMemoryUserRepository.save(new User(account, password, email));

        return redirectToHomeWithLoggedIn();
    }

    private HttpResponse serverError() {
        final HttpStatusLine statusLine =
                new HttpStatusLine(HTTP_VERSION, 500, "Internal Server Error");

        try {
            return readResource(requiredResource("static/500.html"), statusLine);
        } catch (IOException | RuntimeException e) {
            log.error("500.html을 읽지 못했습니다.", e);

            return new HttpResponse(statusLine, Map.of(CONTENT_LENGTH, "0"), new byte[0]);
        }
    }

    private HttpResponse redirectTo(final String location) {
        return new HttpResponse(
                new HttpStatusLine(HTTP_VERSION, 302, "Found"),
                Map.of(LOCATION, location),
                new byte[0]
        );
    }

    private HttpResponse redirectToHomeWithLoggedIn() {
        final Map<String, String> headers = Map.of(
                LOCATION, "/index.html",
                SET_COOKIE, JSESSIONID + "=" + UUID.randomUUID()
        );

        return new HttpResponse(
                new HttpStatusLine(HTTP_VERSION, 302, "Found"),
                headers,
                new byte[0]
        );
    }
}
