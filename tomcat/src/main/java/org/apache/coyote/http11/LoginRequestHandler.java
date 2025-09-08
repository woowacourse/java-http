package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl().startsWith("/login");
    }

    @Override
    public String response(final HttpRequest httpRequest) {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        Path resourcePath = Path.of(resource.getPath());
        byte[] bytes = readAllBytes(resourcePath);

        if (httpRequest.getParameter("account") != null && httpRequest.getParameter("password") != null) {
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
            if (foundUser.isEmpty()) {
                log.info("존재하지 않는 user입니다.");
                return createRedirectResponse("http://localhost:8080/401.html");
            }

            User user = foundUser.get();
            if (!user.checkPassword(httpRequest.getParameter("password"))) {
                log.info("비밀번호 틀림");
                return createRedirectResponse("http://localhost:8080/401.html");
            }

            log.info("user = {}", user);
            return createRedirectResponse("http://localhost:8080/index.html");
        }

        return createSuccessResponse(bytes);
    }

    private byte[] readAllBytes(final Path resourcePath) {
        try {
            return Files.readAllBytes(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createSuccessResponse(final byte[] bytes) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                new String(bytes));
    }

    private String createRedirectResponse(final String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: " + 0 + " ",
                "Location: " + redirectUrl + " ",
                "");
    }
}
