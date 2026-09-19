package org.apache.coyote.http11.pageController;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class RegisterController extends AbstractController {
    private static final String REGISTER_PAGE = "/register.html";
    private static final String SUCCESS_PAGE = "/index.html";

    private final StaticResourceLoader staticResourceLoader = new StaticResourceLoader();

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        return HttpResponse.of(HttpStatus.OK, staticResourceLoader.load(REGISTER_PAGE));
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        String account = httpRequest.getBodyParams("account");
        String email = httpRequest.getBodyParams("email");
        String password = httpRequest.getBodyParams("password");

        if (isInvalidInput(account, email, password) || isDuplicated(account)) {
            return HttpResponse.of(HttpStatus.OK, staticResourceLoader.load(REGISTER_PAGE));
        }

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        HttpResponse response = redirect(SUCCESS_PAGE);
        response.addCookie(SESSION_COOKIE_NAME, newSessionId());

        return response;
    }

    private boolean isInvalidInput(String account, String email, String password) {
        return isBlank(account) || isBlank(email) || isBlank(password);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isDuplicated(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }
}
