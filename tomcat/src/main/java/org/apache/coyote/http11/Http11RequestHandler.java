package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Http11RequestHandler {

    private static final String ROOT_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final Map<String, String> ACCESS_URI = Map.of(
            "/login", "/login.html",
            "/register", "/register.html"
    );

    public static Http11Response handle(Http11RequestHeader http11RequestHeader) {
        RequestUri requestUri = http11RequestHeader.getRequestUri();
        HttpVersion httpVersion = http11RequestHeader.getHttpVersion();
        List<String> acceptTypes = http11RequestHeader.getAcceptType();

        if (requestUri.hasQueryParameters()) {
            return handleWithQueryParameters(requestUri, httpVersion, acceptTypes);
        }

        return getStaticResource(requestUri.getRequestUri())
                .map(staticResource -> getHttp11Response(staticResource, StatusLine.ok(httpVersion), acceptTypes))
                .orElseGet(() -> {
                    String notFoundResource = getStaticResource("/404.html").orElse("404 Not Found");
                    return getHttp11Response(notFoundResource, StatusLine.notFound(httpVersion), acceptTypes);
                });
    }

    private static Http11Response handleWithQueryParameters(RequestUri requestUri,
                                                            HttpVersion httpVersion,
                                                            List<String> acceptTypes) {
        Map<String, String> queryParameters = requestUri.getQueryParameters();

        if (!queryParameters.containsKey("account")) {
            return getHttp11Response(getStaticResource("/401.html").orElse("401 Unauthorized"),
                    StatusLine.unAuthorized(httpVersion), acceptTypes);
        }

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return getHttp11Response(getStaticResource("/401.html").orElse("401 Unauthorized"),
                    StatusLine.unAuthorized(httpVersion), acceptTypes);
        }

        return getHttp11Response(getStaticResource("/index.html").orElse("Index"), StatusLine.found(httpVersion),
                acceptTypes);
    }

    private static Http11Response getHttp11Response(String staticResource,
                                                    StatusLine httpVersion,
                                                    List<String> acceptTypes) {
        Http11ResponseBody responseBody = Http11ResponseBody.of(staticResource);
        Http11ResponseHeader header = Http11ResponseHeader.of(httpVersion,
                ContentType.from(acceptTypes),
                responseBody.getContentLength());

        return Http11Response.of(header, responseBody);
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
