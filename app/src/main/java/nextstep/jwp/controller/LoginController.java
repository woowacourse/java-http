package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final String URI_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String SUCCESS_REDIRECT_URL = "/index.html";
    private static final String FAIL_REDIRECT_URL = "/401.html";

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATH.equals(httpRequest.getPath());
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        if (session != null && session.getUser() != null) {
            return super.redirect(SUCCESS_REDIRECT_URL);
        }
        return super.renderPage(httpRequest);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        try {
            User validatedUser = getValidatedUser(httpRequest.getRequestBody());
            HttpSession session = httpRequest.getSession();
            session.setUser(validatedUser);

            return super.redirect(SUCCESS_REDIRECT_URL);
        } catch (IllegalArgumentException e) {
            return super.redirect(FAIL_REDIRECT_URL);
        }
    }

    private User getValidatedUser(RequestBody requestBody) {
        String account = requestBody.getValue(ACCOUNT);
        String password = requestBody.getValue(PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(IllegalArgumentException::new);

        validateUserInput(password, user);

        return user;
    }

    private void validateUserInput(String password, User user) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException();
        }
    }
}
