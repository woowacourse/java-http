package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Http11RequestHandler {

    private static final String ROOT_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";

    public static Http11ResponseBody handle(RequestUri requestUri) throws IOException {
        if (requestUri.hasQueryParameters()) {
            return handleWithQueryParameters(requestUri);
        }

        String staticResource = getStaticResource(requestUri.getRequestUri());
        return Http11ResponseBody.of(staticResource);
    }

    private static Http11ResponseBody handleWithQueryParameters(RequestUri requestUri) throws IOException {
        Map<String, String> queryParameters = requestUri.getQueryParameters();
        if (!queryParameters.containsKey("account")) {
            return Http11ResponseBody.of(getStaticResource("/401.html"));
        }

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return Http11ResponseBody.of(getStaticResource("/401.html"));
        }

        return Http11ResponseBody.of(getStaticResource("/index.html"));
    }

    private static String getStaticResource(String resourcePath) throws IOException {
        if (ROOT_PATH.equals(resourcePath)) {
            return "Hello world!";
        }

        if ("/login".equals(resourcePath)) {
            resourcePath = "/login.html";
        }

        try (InputStream inputStream = Http11Response.class.getClassLoader()
                .getResourceAsStream(STATIC_RESOURCE_PATH + resourcePath)) {
            if (inputStream == null) {
                return new String(Objects.requireNonNull(Http11Response.class.getClassLoader()
                        .getResourceAsStream(STATIC_RESOURCE_PATH + "/404.html")).readAllBytes(),
                        StandardCharsets.UTF_8);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

