package org.apache.coyote.http11.httpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseBody {

    private static final Logger log = LoggerFactory.getLogger(ResponseBody.class);
    private static final String DEFAULT_RESOURCE_PATH = "static";

    private final HttpRequest httpRequest;

    public ResponseBody(
            final HttpRequest httpRequest
    ) {
        this.httpRequest = httpRequest;
    }

    public ResponseContent getContent() throws IOException {
        final String path = httpRequest.getPath();
        if (path.equals("/")) {
            return ResponseContent.success("Hello world!");
        }

        final Map<String, String> params = httpRequest.getParamsFromBody();
        if (!params.isEmpty()) {
            if ("/login".equals(path)) {
                final String account = params.get("account");
                final String password = params.get("password");

                final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
                if (userOrEmpty.isPresent()) {
                    final User user = userOrEmpty.get();
                    log.info("user: {}", user);

                    if (!user.checkPassword(password)) {
                        final String body = getBodyFromStaticFile("/401.html");
                        return ResponseContent.redirect(body, "/401.html");
                    }

                    final String body = getBodyFromStaticFile("/index.html");
                    return ResponseContent.redirect(body, "/index.html");
                }
            }

            if ("/register".equals(path)) {
                final String account = params.get("account");
                final String email = params.get("email");
                final String password = params.get("password");

                final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
                if (userOrEmpty.isPresent()) {
                    log.warn("id: {}", account);
                    throw new IllegalArgumentException("이미 가입된 계정입니다.");
                }

                final User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                final String body = getBodyFromStaticFile("/index.html");
                return ResponseContent.redirect(body, "/index.html");
            }
        }

        String filePath = DEFAULT_RESOURCE_PATH + path;
        if (!path.contains(".")) {
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

    private ResponseContent createHttpResponseContentFrom(
            String filePath
    ) throws IOException {
        URL resource  = getClass().getClassLoader().getResource(filePath);
        if (resource == null) {
            final String body = getBodyFromStaticFile("/404.html");
            return ResponseContent.error(body);
        }

        final String body = getBodyFromResource(resource);
        return ResponseContent.success(body);
    }
}
