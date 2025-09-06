package org.apache.catalina;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class LoginHandler {

    public HttpResponse login(HttpRequest request) {
        String account = request.getQueryParameter("account");
        String password = request.getQueryParameter("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user: {}", user.get());
            return new HttpResponse(HttpStatusCode.FOUND, ContentType.HTML, "/index.html");
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/401.html");
    }
}
