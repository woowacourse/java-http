package com.techcourse.servlet;

import com.techcourse.application.AuthService;
import com.techcourse.application.dto.LoginRequest;
import com.techcourse.exception.BusinessException;
import com.techcourse.model.User;
import com.techcourse.servlet.util.StaticFileLoader;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {
    private static final String LOGIN_PAGE = "static/login.html";
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    public static final String USER_ACCOUNT = "userAccount";

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            Session session = findValidSession(request);
            if (session != null) {
                sendRedirect(response, "/index.html");
                return;
            }

            byte[] content = StaticFileLoader.loadStaticFile(LOGIN_PAGE);
            response.setContentType(ContentType.fromPath(LOGIN_PAGE));
            response.appendToBody(content);
        } catch (IOException e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> bodyParams = request.getBodyParams();
        String account = bodyParams.get("account");
        String password = bodyParams.get("password");

        try {
            User user = authService.login(new LoginRequest(account, password));
            addSessionAndCookie(request, response, user);
            sendRedirect(response, "/index.html");

            log.info("로그인 성공! 아이디 : {}", account);
        } catch (BusinessException e) {
            sendRedirect(response, "/401.html");
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    private void sendRedirect(HttpResponse response, String location) {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.addToHeader("Location", location);
    }

    private Session findValidSession(HttpRequest request) {
        if (!request.hasJSessionCookie()) {
            return null;
        }
        Session session = sessionManager.findSession(request.getJSessionId());
        if (session == null) {
            return null;
        }
        Object account = session.getAttribute(USER_ACCOUNT);
        if (account == null) {
            return null;
        }
        authService.loginCheck(String.valueOf(account));
        return session;
    }

    private void addSessionAndCookie(HttpRequest request, HttpResponse response, User user) {
        if (request.hasJSessionCookie()) {
            return;
        }
        Session session = new Session();
        session.setAttribute(USER_ACCOUNT, user.getAccount());
        sessionManager.add(session);

        HttpCookie cookie = new HttpCookie();
        cookie.addJSessionId(session.getId());
        response.addToHeader("Set-Cookie", cookie.toHeaderString());
    }
}
