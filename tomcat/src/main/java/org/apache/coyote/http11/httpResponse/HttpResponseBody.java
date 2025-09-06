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

    public HttpResponseContent getContent() throws IOException {
        if (requestUri.equals("/")) {
            return HttpResponseContent.success("Hello world!");
        }

        String filePath = DEFAULT_RESOURCE_PATH + requestUri;

        if (requestUri.contains("?")) {
            int index = requestUri.indexOf("?");
            filePath = DEFAULT_RESOURCE_PATH + requestUri.substring(0, index);

            final String queryString = requestUri.substring(index + 1);
            final String[] queryStrings = queryString.split("&");

            final Map<String, String> params = new HashMap<>();
            for (String param : queryStrings) {
                final String name = param.split("=")[0];
                final String value = param.split("=")[1];
                params.put(name, value);
            }

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(params.get("account"));
            if (userOrEmpty.isPresent()) {
                final User user = userOrEmpty.get();
                log.info("user: {}", user);

                if (!user.checkPassword(params.get("password"))) {
                    final String body = getBodyFromStaticFile("/401.html");
                    return HttpResponseContent.redirect(body, "/401.html");
                }

                final String body = getBodyFromStaticFile("/index.html");
                return HttpResponseContent.redirect(body, "/index.html");
            }
        }

        if (!requestUri.contains(".")) {
            filePath += ".html";
        }

        return createHttpResponseContentFrom(filePath);
    }

    private String getBodyFromStaticFile(final String fileName) throws IOException {
        final String filePath = DEFAULT_RESOURCE_PATH + fileName;
        final URL resource = getClass().getClassLoader().getResource(filePath);
        return getBodyFromResource(resource);
    }

    private String getBodyFromResource(final URL resource) throws IOException {
        final File file = new File(resource.getFile());
        final Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }

    private HttpResponseContent createHttpResponseContentFrom(
            String filePath
    ) throws IOException {
        URL resource  = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            final String body = getBodyFromStaticFile("/404.html");
            return HttpResponseContent.error(body);
        }

        final String body = getBodyFromResource(resource);
        return HttpResponseContent.success(body);
    }
}
