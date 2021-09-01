package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class LoginController extends AbstractController {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String LOGIN_PAGE_PATH = "login.html";
    private static final String LOGIN_SUCCESS_PATH = "index.html";
    private static final String LOGIN_FAILURE_PATH = "401.html";

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) throws URISyntaxException, IOException {
        if (request.hasQueryParams()) {
            String account = request.getQueryParam("account");
            String password = request.getQueryParam("password");
            return InMemoryUserRepository.findByAccount(account)
                    .map(user -> requestLogin(user, password))
                    .orElseGet(this::loginFail);
        }

        String resourceUri = RESOURCE_PREFIX + LOGIN_PAGE_PATH;
        String resourceFile = findResourceFile(resourceUri);
        return JwpHttpResponse.ok(resourceUri, resourceFile);
    }

    private JwpHttpResponse requestLogin(User user, String password) {
        if (user.checkPassword(password)) {
            return JwpHttpResponse.found(LOGIN_SUCCESS_PATH);
        }

        return loginFail();
    }

    private JwpHttpResponse loginFail() {
        return JwpHttpResponse.found(LOGIN_FAILURE_PATH);
    }
}
