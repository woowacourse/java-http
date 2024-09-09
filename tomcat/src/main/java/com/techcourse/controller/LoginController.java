package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.Handler;
import org.apache.catalina.controller.HttpEndpoint;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Queries;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseCookie;
import org.apache.coyote.http11.response.ResponseFile;

public class LoginController extends AbstractController {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final HttpEndpoint LOGIN_POST = new HttpEndpoint("/login", HttpMethod.POST);
    private static final HttpEndpoint LOGIN_GET = new HttpEndpoint("/login", HttpMethod.GET);
    private static final String LOGIN_PAGE = "/login.html";

    public LoginController() {
        List<Handler> handlers = List.of(
                new Handler(LOGIN_POST, this::doLoginPost),
                new Handler(LOGIN_GET, this::doLoginGet)
        );
        registerHandlers(handlers);
    }

    private void doLoginPost(HttpRequest request, HttpResponse response) {
        String requestBody = request.getBody();
        Queries queries = Queries.of(requestBody);
        String account = queries.get("account");
        String password = queries.get("password");
        validateLoginRequest(account, password);

        HttpSession session = request.getSession();

        User user = getUser(account, password);
        session.setAttribute("user", user);

        if (SESSION_MANAGER.findSession(session.getId()) == null) {
            SESSION_MANAGER.add(session);
            response.addCookie(ResponseCookie.of(session));
        }

        response.setHttpStatus(HttpStatus.FOUND);
        response.addRedirectHeader("/index.html");
    }

    private void validateLoginRequest(String account, String password) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("account는 비어 있을 수 없습니다.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password는 비어 있을 수 없습니다.");
        }
    }

    private static User getUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }
        return user;
    }

    private void doLoginGet(HttpRequest request, HttpResponse response) {
        if (isLoginUser(request)) {
            response.setHttpStatus(HttpStatus.FOUND);
            response.addRedirectHeader("/index.html");
            return;
        }
        ResponseFile responseFile = ResponseFile.of(LOGIN_PAGE);
        response.setHttpStatus(HttpStatus.OK);
        response.addFile(responseFile);
    }

    private boolean isLoginUser(HttpRequest request) {
        HttpSession requestSession = request.getSession();
        HttpSession managedSession = SESSION_MANAGER.findSession(requestSession.getId());
        return managedSession != null && managedSession.getAttribute("user") != null;
    }
}
