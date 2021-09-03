package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.common.HttpSession;
import nextstep.jwp.http.common.HttpSessions;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;
import nextstep.jwp.http.http_response.StatusCode;
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
        if (request.hasSession()) {
            HttpSession session = request.getSession();
            System.out.println("재접속하는 sessionId " + session.getId());
            return requestPageByUserInfo(session);
        }

        if (request.hasQueryParams()) {
            return requestLogin(request);
        }

        return requestLoginPage();
    }

    private JwpHttpResponse requestPageByUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !InMemoryUserRepository.isExistAccount(user.getAccount())) {
            session.removeAttribute("user");
            return JwpHttpResponse.found(LOGIN_PAGE_PATH);
        }

        return JwpHttpResponse.found(LOGIN_SUCCESS_PATH);
    }

    private JwpHttpResponse requestLogin(JwpHttpRequest request) {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> tryLogin(user, password))
                .orElseGet(this::loginFail);
    }

    private JwpHttpResponse tryLogin(User user, String password) {
        if (user.checkPassword(password)) {
            HttpSession httpSession = HttpSessions.generate();
            httpSession.setAttribute("user", user);
            System.out.println("저장하는 sessionId " + httpSession.getId());
            return new JwpHttpResponse.Builder()
                    .statusCode(StatusCode.FOUND)
                    .cookie(HttpSessions.generate().getId())
                    .location(LOGIN_SUCCESS_PATH)
                    .build();
        }

        return loginFail();
    }

    private JwpHttpResponse loginFail() {
        return JwpHttpResponse.found(LOGIN_FAILURE_PATH);
    }

    private JwpHttpResponse requestLoginPage() throws URISyntaxException, IOException {
        String resourceUri = RESOURCE_PREFIX + LOGIN_PAGE_PATH;
        String resourceFile = findResourceFile(resourceUri);
        JwpHttpResponse response = JwpHttpResponse.ok(resourceUri, resourceFile);
        return response;
    }
}
