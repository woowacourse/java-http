package org.apache.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.controller.FileReader.FileReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class LoginController extends AbstractController {

    private static final String URL = "/login";
    private static final Set<HttpMethod> AVAILABLE_HTTP_METHODS = Set.of(HttpMethod.GET, HttpMethod.POST);
    private static final String JSESSIONID = "JSESSIONID";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER_SESSION_KEY = "user";

    public LoginController() {
        super(URL, AVAILABLE_HTTP_METHODS);
    }

    @Override
    protected void doGet(Request request, Response response) {
        Session session = request.getSession(false);
        if (hasUserSession(session)) {
            redirectPage(response, HttpStatus.FOUND, INDEX_PATH);
            return;
        }
        getLoginPage(request, response);
    }

    private void getLoginPage(Request request, Response response) {
        FileReader fileReader = FileReader.from(request.getPath());
        String body = fileReader.read();

        makeResourceResponse(request, response, HttpStatus.OK, body);
    }

    private boolean hasUserSession(Session session) {
        return session != null && session.getAttribute(USER_SESSION_KEY) != null;
    }

    @Override
    protected void doPost(Request request, Response response) {
        Session session = request.getSession(false);
        if (hasUserSession(session)) {
            doSessionLogin(response, session);
            return;
        }
        doLogin(request, response);
    }

    private void doSessionLogin(Response response, Session session) {
        User user = (User) session.getAttribute(USER_SESSION_KEY);
        System.out.println(user);

        redirectPage(response, HttpStatus.FOUND, INDEX_PATH);
    }

    private void doLogin(Request request, Response response) {
        Map<String, String> requestBody = request.getBody();
        String account = requestBody.get(ACCOUNT);
        String password = requestBody.get(PASSWORD);

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
        if (isInvalidLogin(password, loginUser)) {
            redirectPage(response, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_PATH);
            return;
        }
        User user = loginUser.get();
        System.out.println(user);

        registerSession(request, response, user);
        redirectPage(response, HttpStatus.FOUND, INDEX_PATH);
    }

    private boolean isInvalidLogin(String password, Optional<User> loginUser) {
        return loginUser.isEmpty() || !loginUser.get().checkPassword(password);
    }

    private void registerSession(Request request, Response response, User user) {
        Session session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, user);
        response.addCookie(JSESSIONID, session.getId());
    }
}
