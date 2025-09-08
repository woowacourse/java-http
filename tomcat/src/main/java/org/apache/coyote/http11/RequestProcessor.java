package org.apache.coyote.http11;

import com.techcourse.Application;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
        } else if (resourcePath.isQueryString()) {
            final String resourcePathValue = resourcePath.value();
            final int startIndex = resourcePathValue.indexOf("?");
            final String queryString = resourcePathValue.substring(startIndex + 1);
            final Map<String, String> parsedQueryString = parseQueryString(queryString);
            return processRequestByQueryString(httpRequest, parsedQueryString);
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
            final HttpCookie httpCookie = new HttpCookie(httpRequest.getHeader().get("Cookie"));
            final Optional<Session> session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
            if (session.isPresent()) {
                if (session.get().contains("user")) {
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.TEXT, null);
                    httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                    return httpResponse;
                }
            }
            final HttpStatus statusCode = HttpStatus.OK;
            final ContentType contentType = ContentType.HTML;
            final String body = readFile("login.html");
            return new HttpResponse(statusCode, contentType, body);
        } else if (path.equals("/register")) {
            if (requestLine.method().equals(HttpMethod.GET)) {
                final HttpStatus statusCode = HttpStatus.OK;
                final ContentType contentType = ContentType.HTML;
                final String body = readFile("register.html");
                return new HttpResponse(statusCode, contentType, body);
            } else if (requestLine.method().equals(HttpMethod.POST)) {
                final HttpStatus statusCode = HttpStatus.FOUND;
                final ContentType contentType = ContentType.HTML;
                final Map<String, String> requestBody = httpRequest.parseBody();
                InMemoryUserRepository.save(new User(
                        2L,
                        requestBody.get("account"),
                        requestBody.get("password"),
                        requestBody.get("email")
                ));
                HttpResponse httpResponse = new HttpResponse(statusCode, contentType, null);
                httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                return httpResponse;
            }
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
    }

    private HttpResponse processRequestByQueryString(HttpRequest httpRequest, Map<String, String> queryStrings) {
        String path = httpRequest.getRequestLine().resourcePath().value();
        if (path.startsWith("/login")) {
            HttpCookie httpCookie = new HttpCookie(httpRequest.getHeader().get("Cookie"));
            Optional<Session> session = sessionManager.findSession(httpCookie.get("JSESSIONID"));
            if (session.isPresent() && session.get().contains("user")) {
                HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, ContentType.TEXT, null);
                httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
                return httpResponse;
            }
            String account = queryStrings.get("account");
            final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
            if (!optionalUser.isPresent()) {
                return loginFailed();
            }
            final User user = optionalUser.get();
            if (!user.checkPassword(queryStrings.get("password"))) {
                return loginFailed();
            }
            final HttpStatus statusCode = HttpStatus.FOUND;
            final ContentType contentType = ContentType.HTML;
            HttpResponse httpResponse = new HttpResponse(statusCode, contentType, null);
            UUID sessionId = UUID.randomUUID();
            httpResponse.appendHeader("Set-Cookie", String.format("JSESSIONID=%s", sessionId));
            Session newSession = new Session(sessionId.toString());
            sessionManager.add(newSession);
            newSession.setAttribute("user", user);
            httpResponse.appendHeader("Location", "http://localhost:8080/index.html");
            return httpResponse;
        }
        return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.TEXT, null);
    }

    private HttpResponse loginFailed() {
        final HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
        final ContentType contentType = ContentType.HTML;
        final HttpResponse httpResponse = new HttpResponse(statusCode, contentType, null);
        httpResponse.appendHeader("Location", "http://localhost:8080/401.html");
        return httpResponse;
    }

    private Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> parsed = new HashMap<>();
        final String[] fields = queryString.split("&");
        for (String field : fields) {
            final int delimiterIndex = field.indexOf("=");
            final String fieldName = field.substring(0, delimiterIndex);
            final String fieldValue = field.substring(delimiterIndex + 1);
            parsed.put(fieldName, fieldValue);
        }
        return parsed;
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
