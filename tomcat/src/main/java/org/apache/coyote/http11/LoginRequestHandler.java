package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET &&
               request.getUri().toString().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        Optional<User> verifiedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (verifiedUser.isPresent()) {
            log.info("Verified user: {}", verifiedUser);
        }
        byte[] body = StaticResourceLoader.load("/login.html");
        return HttpResponse.builder()
                .ok()
                .contentType(ContentType.TEXT_HTML)
                .body(new String(body))
                .build();
    }
}
