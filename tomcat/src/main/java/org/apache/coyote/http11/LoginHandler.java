package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

class LoginHandler implements ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path);
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        QueryParameter queryParameter = new QueryParameter(httpRequest.getUri().getQuery());
        checkUser(queryParameter);

        String resourcePath = getClass().getClassLoader().getResource("static/login.html").getPath();
        byte[] bytes = readStaticResource(resourcePath);

        return new HttpResponse(bytes, "text/html;charset=utf8");
    }

    private void checkUser(QueryParameter queryParameter) {
        String password = queryParameter.get("password").orElse("");
        Optional<User> user = queryParameter.get("account").flatMap(InMemoryUserRepository::findByAccount);

        if (user.isPresent()) {
            boolean isSame = user.get().checkPassword(password);
            if (isSame) {
                log.info("{}", user.get());
            }
        }
    }

    private byte[] readStaticResource(String resourcePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(resourcePath))) {
            return bufferedInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
