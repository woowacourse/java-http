package org.apache.coyote.http11;

import com.techcourse.Application;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.constant.ContentType;
import org.apache.coyote.http11.constant.HttpCookie;
import org.apache.coyote.http11.constant.HttpMethod;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.constant.RequestLine;
import org.apache.coyote.http11.constant.ResourcePath;
import org.apache.coyote.util.StreamReader;

public class RequestProcessor {

    private final SessionManager sessionManager = new SessionManager();

    public RequestProcessor() {

    }

    public HttpResponse generateResponse(HttpRequest httpRequest) {
        if (httpRequest == null) {
            return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
        }
        final RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine == null) {
            return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
        }
        final ResourcePath resourcePath = requestLine.resourcePath();
        if (resourcePath.isStaticResource()) {
            final HttpStatus statusCode = HttpStatus.OK;
            final String body = readFile(resourcePath.value());
            return new HttpResponse(statusCode, resourcePath.extractContentType(), body);
        } else if (resourcePath.value().equals("/")) {
            final HttpStatus statusCode = HttpStatus.OK;
            final ContentType contentType = ContentType.HTML;
            final String body = readFile("index.html");
            return new HttpResponse(statusCode, contentType, body);
        } else {
            return processRequest(httpRequest);
        }
    }

    public HttpResponse processRequest(HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.getRequestLine();
        final String path = requestLine.resourcePath().value();
        if (path.equals("/login")) {
            if (requestLine.method() == HttpMethod.GET) {
                final HttpCookie httpCookie = new HttpCookie(httpRequest.getHeader().get("Cookie"));
                final Optional<Session> session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
                if (session.isPresent() && session.get().contains("user")) {
                    final HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.TEXT, null);
                    httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                    return httpResponse;
                }
                return new HttpResponse(HttpStatus.OK, ContentType.HTML, readFile("login.html"));
            } else if (requestLine.method() == HttpMethod.POST) {
                final Map<String, String> body = httpRequest.parseBody();
                final String account = body.get("account");
                final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
                if (!optionalUser.isPresent()) {
                    return loginFailed();
                }
                final User user = optionalUser.get();
                if (!user.checkPassword(body.get("password"))) {
                    return loginFailed();
                }
                final String sessionId = generateSession(user);
                final HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.HTML, null);
                httpResponse.appendHeader("Set-Cookie", String.format("JSESSIONID=%s", sessionId));
                httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                return httpResponse;
            }
        } else if (path.equals("/register")) {
            if (requestLine.method().equals(HttpMethod.GET)) {
                return new HttpResponse(HttpStatus.OK, ContentType.HTML, readFile("register.html"));
            } else if (requestLine.method().equals(HttpMethod.POST)) {
                saveUser(httpRequest);
                HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.HTML, null);
                httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                return httpResponse;
            }
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
    }

    private String generateSession(User user) {
        final UUID sessionId = UUID.randomUUID();
        final Session newSession = new Session(sessionId.toString());
        sessionManager.add(newSession);
        newSession.setAttribute("user", user);
        return sessionId.toString();
    }

    private void saveUser(HttpRequest httpRequest) {
        final Map<String, String> requestBody = httpRequest.parseBody();
        InMemoryUserRepository.save(new User(
                2L,
                requestBody.get("account"),
                requestBody.get("password"),
                requestBody.get("email")
        ));
    }

    private HttpResponse loginFailed() {
        final HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
        final ContentType contentType = ContentType.HTML;
        final HttpResponse httpResponse = new HttpResponse(statusCode, contentType, null);
        httpResponse.appendHeader("Location", "http://localhost:8080/401.html");
        return httpResponse;
    }

    private String readFile(String path) {
        final String resourcePath = String.format("static/%s", path);
        try (InputStream resourceAsStream = Application.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return StreamReader.readFile(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
