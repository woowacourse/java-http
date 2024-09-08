package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final Map<String, String> ACCESS_URI = Map.of(
            "/login", "/login.html",
            "/register", "/register.html"
    );

    public static Http11Response handle(Http11Request http11Request) {
        Http11RequestHeader http11RequestHeader = http11Request.getHttp11RequestHeader();
        Http11RequestBody http11RequestBody = http11Request.getHttp11RequestBody();

        RequestUri requestUri = http11RequestHeader.getRequestUri();
        HttpVersion httpVersion = http11RequestHeader.getHttpVersion();
        List<String> acceptTypes = http11RequestHeader.getAcceptType();
        HttpMethod httpMethod = http11RequestHeader.getHttpMethod();

        if (httpMethod.isPost()) {
            if (requestUri.matches("/login")) {
                return handleLogin(http11RequestBody, httpVersion, acceptTypes);
            } else if (requestUri.matches("/register")) {
                return handleRegister(http11RequestBody, httpVersion, acceptTypes);
            }
        }

        if (requestUri.hasQueryParameters()) {
            Map<String, String> queryParameters = requestUri.getQueryParameters();
            if (queryParameters.containsKey("account") && queryParameters.containsKey("password")) {
                String account = queryParameters.get("account");
                String password = queryParameters.get("password");
                return processLogin(httpVersion, acceptTypes, account, password);
            }
        }

        String cookie = http11RequestHeader.getCookie();
        Http11Cookie http11Cookie = Http11Cookie.from(cookie);
        if (!http11Cookie.isEmpty() && requestUri.matches("/login")) {
            // 세션
        }

        return getStaticResource(requestUri.getRequestUri())
                .map(staticResource -> getHttp11Response(staticResource, acceptTypes, StatusLine.ok(httpVersion)))
                .orElseGet(() -> getHttp11Response(getStaticResource("/404.html").orElse("404 Not Found"), acceptTypes,
                        StatusLine.notFound(httpVersion)));
    }

    private static Http11Response handleLogin(Http11RequestBody http11RequestBody,
                                              HttpVersion httpVersion,
                                              List<String> acceptTypes) {
        Map<String, String> loginData = parseBody(http11RequestBody.getBody());
        String account = loginData.get("account");
        String password = loginData.get("password");

        return processLogin(httpVersion, acceptTypes, account, password);
    }

    private static Http11Response processLogin(HttpVersion httpVersion, List<String> acceptTypes, String account,
                                               String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return getHttp11Response(getStaticResource("/401.html").orElse("401 Unauthorized"), acceptTypes,
                    StatusLine.unAuthorized(httpVersion));
        }

        log.info("로그인 성공! 아이디 : {}", account);

        // Header에 Set-Cookie로 JSSESIONID를 줘야함.
        Http11ResponseBody responseBody = Http11ResponseBody.of(getStaticResource("/index.html").orElse("Index"));
        int contentLength = responseBody.getContentLength();
        String contentType = ContentType.from(acceptTypes).getContentType();
        HttpHeaders httpHeaders = HttpHeaders.of(
                Map.of(CONTENT_TYPE, List.of(contentType),
                        CONTENT_LENGTH, List.of(String.valueOf(contentLength)),
                        "Location", List.of("/index.html"),
                        "Set-Cookie", List.of("JSESSIONID=" + UUID.randomUUID())
                ),
                (s1, s2) -> true);
        Http11ResponseHeader header = Http11ResponseHeader.of(StatusLine.found(httpVersion), httpHeaders);

        return Http11Response.of(header, responseBody);
    }

    private static HttpHeaders getHttpHeaders(String contentType, int contentLength) {
        return HttpHeaders.of(
                Map.of(CONTENT_TYPE, List.of(contentType),
                        CONTENT_LENGTH, List.of(String.valueOf(contentLength))),
                (s1, s2) -> true);
    }

    private static Http11Response handleRegister(Http11RequestBody http11RequestBody,
                                                 HttpVersion httpVersion,
                                                 List<String> acceptTypes) {
        Map<String, String> registrationData = parseBody(http11RequestBody.getBody());
        String account = registrationData.get("account");
        String password = registrationData.get("password");
        String email = registrationData.get("email");

        // db에서 찾아서 있는 아이디면 예외
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return redirectToRegister(httpVersion, acceptTypes);
        }

        try {
            User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
        } catch (Exception e) {
            return redirectToRegister(httpVersion, acceptTypes);
        }

        Http11ResponseBody responseBody = Http11ResponseBody.of(getStaticResource("/index.html").orElse("Index"));
        int contentLength = responseBody.getContentLength();
        String contentType = ContentType.from(acceptTypes).getContentType();
        HttpHeaders httpHeaders = HttpHeaders.of(
                Map.of(CONTENT_TYPE, List.of(contentType),
                        CONTENT_LENGTH, List.of(String.valueOf(contentLength)),
                        "Location", List.of("/index.html")
                ),
                (s1, s2) -> true);
        Http11ResponseHeader header = Http11ResponseHeader.of(StatusLine.found(httpVersion), httpHeaders);

        return Http11Response.of(header, responseBody);
    }

    private static Http11Response redirectToRegister(HttpVersion httpVersion, List<String> acceptTypes) {
        Http11ResponseBody responseBody = Http11ResponseBody.of(
                getStaticResource("/register.html").orElse("400 Bad Request"));
        int contentLength = responseBody.getContentLength();
        String contentType = ContentType.from(acceptTypes).getContentType();
        HttpHeaders httpHeaders = HttpHeaders.of(
                Map.of(CONTENT_TYPE, List.of(contentType),
                        CONTENT_LENGTH, List.of(String.valueOf(contentLength)),
                        "Location", List.of("/register.html")),
                (s1, s2) -> true);
        Http11ResponseHeader header = Http11ResponseHeader.of(StatusLine.found(httpVersion), httpHeaders);

        return Http11Response.of(header, responseBody);
    }

    private static Http11Response getHttp11Response(String Index, List<String> acceptTypes, StatusLine httpVersion) {
        Http11ResponseBody responseBody = Http11ResponseBody.of(Index);
        int contentLength = responseBody.getContentLength();
        String contentType = ContentType.from(acceptTypes).getContentType();
        HttpHeaders httpHeaders = getHttpHeaders(contentType, contentLength);
        Http11ResponseHeader header = Http11ResponseHeader.of(httpVersion, httpHeaders);

        return Http11Response.of(header, responseBody);
    }

    private static Map<String, String> parseBody(String body) {
        List<String> params = Arrays.asList(body.split("&"));
        return params.stream()
                .map(param -> Arrays.asList(param.split("=")))
                .collect(Collectors.toMap(
                        param -> param.get(0),
                        param -> param.size() > 1 ? param.get(1) : ""
                ));
    }

    private static Optional<String> getStaticResource(String resourcePath) {
        if (ROOT_PATH.equals(resourcePath)) {
            return Optional.of("Hello world!");
        }

        resourcePath = getAccessUri(resourcePath);

        try (InputStream inputStream = Http11RequestHandler.class.getClassLoader()
                .getResourceAsStream(STATIC_RESOURCE_PATH + resourcePath)) {
            if (inputStream == null) {
                return Optional.empty();
            }
            return Optional.of(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static String getAccessUri(String resourcePath) {
        return ACCESS_URI.getOrDefault(resourcePath, resourcePath);
    }
}
