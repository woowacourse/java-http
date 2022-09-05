package org.apache.coyote.http11.response.generator;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginResponseGenerator implements ResponseGenerator {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_FAILURE_REDIRECT_URI = "http://localhost:8080/401.html";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.isLoginRequest();
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) {
        String account = httpRequest.getQueryParamValueOf(ACCOUNT_KEY);
        String password = httpRequest.getQueryParamValueOf(PASSWORD_KEY);

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return responseAfterPasswordCheck(user.get(), password);
        }
        return HttpResponse.found(LOGIN_FAILURE_REDIRECT_URI);
    }

    private HttpResponse responseAfterPasswordCheck(User user, String password) {
        if (user.checkPassword(password)) {
            return HttpResponse.found(LOGIN_SUCCESS_REDIRECT_URI);
        }
        return HttpResponse.found(LOGIN_FAILURE_REDIRECT_URI);
    }
}
