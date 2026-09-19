package org.apache.coyote.http11.pageController;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "/login.html";
    private static final String SUCCESS_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        String account = httpRequest.getQueryParams("account");
        String password = httpRequest.getQueryParams("password");

        if (account == null || password == null) {
            return HttpResponse.of(HttpStatus.OK, staticResourceLoader.load(LOGIN_PAGE));
        }

        Optional<User> loginUser = findLoginUser(account, password);
        if (loginUser.isEmpty()) {
            return redirect(UNAUTHORIZED_PAGE);
        }

        log.info("login user: {}", loginUser.get());
        return redirect(SUCCESS_PAGE);
    }

    private Optional<User> findLoginUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
