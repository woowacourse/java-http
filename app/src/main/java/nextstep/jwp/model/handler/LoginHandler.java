package nextstep.jwp.model.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.http_request.JwpHttpRequest;
import nextstep.jwp.model.http_response.JwpHttpResponse;
import nextstep.jwp.model.user.domain.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class LoginHandler extends DefaultHttpHandler {

    private static final String RESOURCE_PREFIX = "static/";
    private static final String LOGIN_PAGE_PATH = "login.html";
    private static final String LOGIN_SUCCESS_PATH = "index.html";
    private static final String LOGIN_FAILURE_PATH = "401.html";

    @Override
    public String handle(JwpHttpRequest jwpHttpRequest) throws URISyntaxException, IOException {
        if (jwpHttpRequest.isEmptyParams()) {
            String resourceUri = RESOURCE_PREFIX + LOGIN_PAGE_PATH;
            String resourceFile = findResourceFile(resourceUri);
            return JwpHttpResponse.ok(resourceUri, resourceFile);
        }

        String account = jwpHttpRequest.getParam("account");
        String password = jwpHttpRequest.getParam("password");
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> requestLogin(user, password))
                .orElseGet(this::loginFail);
    }

    private String requestLogin(User user, String password) {
        if (user.checkPassword(password)) {
            return JwpHttpResponse.found(LOGIN_SUCCESS_PATH);
        }

        return loginFail();
    }

    private String loginFail() {
        return JwpHttpResponse.found(LOGIN_FAILURE_PATH);
    }
}
