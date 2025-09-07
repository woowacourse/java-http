package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean support(final RequestStartLine requestStartLine) {
        Map<String, String> queryParameters = requestStartLine.queryParameters();

        return requestStartLine.requestMethod() == RequestMethod.GET &&
                requestStartLine.requestUrl().startsWith("/login") &&
                queryParameters.containsKey("account") &&
                queryParameters.containsKey("password");
    }

    @Override
    public String response(final RequestStartLine requestStartLine) {
        Map<String, String> getQueryParameters = requestStartLine.queryParameters();
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());
        byte[] bytes = readAllBytes(resourcePath);

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(getQueryParameters.get("account"));
        if (foundUser.isEmpty()) {
            log.info("존재하지 않는 user입니다.");
            return createHttpResponse(bytes);
        }

        User user = foundUser.get();
        if (user.checkPassword(getQueryParameters.get("password"))) {
            log.info("user = {}", user);
        }

        return createHttpResponse(bytes);
    }

    private byte[] readAllBytes(final Path resourcePath) {
        try {
            return Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createHttpResponse(final byte[] bytes) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }
}
