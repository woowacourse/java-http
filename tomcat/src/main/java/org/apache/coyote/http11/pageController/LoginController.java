package org.apache.coyote.http11.pageController;

import com.techcourse.db.InMemoryUserRepository;
import java.io.IOException;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        logLoginUser(httpRequest);

        return HttpResponse.of(HttpStatus.OK, staticResourceLoader.load("/login.html"));
    }

    private void logLoginUser(HttpRequest httpRequest) {
        String account = httpRequest.getParams("account");
        String password = httpRequest.getParams("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("login user: {}", user));
    }
}
