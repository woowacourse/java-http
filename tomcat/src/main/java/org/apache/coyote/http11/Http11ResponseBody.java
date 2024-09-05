package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Http11ResponseBody {

    private static final String ROOT_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";

    private final String body;

    public Http11ResponseBody(String body) {
        this.body = body;
    }

    public static Http11ResponseBody from(RequestUri requestUri) throws IOException {
        if (requestUri.hasQueryParameters()) {
            Map<String, String> queryParameters = requestUri.getQueryParameters();
            if (!queryParameters.containsKey("account")) {
                return new Http11ResponseBody(getStaticResource("/401.html"));
            }
            String account = queryParameters.get("account");
            String password = queryParameters.get("password");
            Optional<User> user = InMemoryUserRepository.findByAccount(account);

            if (user.isEmpty() || !user.get().checkPassword(password)) {
                return new Http11ResponseBody(getStaticResource("/401.html"));
            }

            return new Http11ResponseBody(getStaticResource("/index.html"));
        }

        String staticResource = getStaticResource(requestUri.getRequestUri());
        return new Http11ResponseBody(staticResource);
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


    public int getContentLength() {
        return body.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public String toString() {
        return body;
    }
}
