package org.apache.coyote.http11.httpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseBody {

    private static final Logger log = LoggerFactory.getLogger(HttpResponseBody.class);
    private static final String DEFAULT_RESOURCE_PATH = "static";

    private final String requestUri;

    public HttpResponseBody(
            final String requestUri
    ) {
        this.requestUri = requestUri;
    }

    public String getBody() throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        if (requestUri.contains("?")) {
            int index = requestUri.indexOf("?");

            final String queryString = requestUri.substring(index + 1);
            final String[] queryStrings = queryString.split("&");

            final Map<String, String> params = new HashMap<>();
            for (String param : queryStrings) {
                final String name = param.split("=")[0];
                final String value = param.split("=")[1];
                params.put(name, value);
            }

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(params.get("account"));
            userOrEmpty.ifPresent(user -> log.info("user: {}", user));

            return getResponseBodyIfHasQueryString(index);
        }

        return getPlainResponseBody();
    }

    private String getResponseBodyIfHasQueryString(final int index) throws IOException {
        final String filePath = DEFAULT_RESOURCE_PATH + requestUri.substring(0, index);
        return readResourceAsString(filePath);
    }

    private String getPlainResponseBody() throws IOException {
        final String filePath = DEFAULT_RESOURCE_PATH + requestUri;
        return readResourceAsString(filePath);
    }

    private String readResourceAsString(
            String filePath
    ) throws IOException {
        if (!requestUri.contains(".")) {
            filePath += ".html";
        }

        URL resource  = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource(DEFAULT_RESOURCE_PATH + "/404.html");
        }
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
