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
            if (request.hasJSessionCookie()) {
                String jSessionId = request.getJSessionId();
                Session session = sessionManager.findSession(jSessionId);
                if (session != null && session.getAttribute(USER_ACCOUNT) != null) {
                    authService.loginCheck(String.valueOf(session.getAttribute(USER_ACCOUNT)));
                    response.setStatus(HttpStatus.SEE_OTHER);
                    response.addToHeader("Location", "/index.html");
                    return;
                }
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

            response.setStatus(HttpStatus.SEE_OTHER);
            addJSessionCookie(request, response, user);
            response.addToHeader("Location", "/index.html");

            log.info("로그인 성공! 아이디 : " + account);
        } catch (BusinessException e) {
            response.setStatus(HttpStatus.SEE_OTHER);
            response.addToHeader("Location", "/401.html");
        } catch (Exception e) {
            ServletExceptionHandler.getInstance().handle(response, e);
        }
    }

    //TODO: 요구 사항을 따르긴했는데 항상 새세션이 필요한건 아닌지 검토  (2025-09-9, 화, 3:46)
    private void addJSessionCookie(HttpRequest request, HttpResponse response, User user) {
        if (!request.hasJSessionCookie()) {
            Session session = new Session();
            session.setAttribute(USER_ACCOUNT, user.getAccount());
            SessionManager.getInstance().add(session);

            HttpCookie cookie = new HttpCookie();
            cookie.addJSessionId(session.getId());

            response.addToHeader("Set-Cookie", cookie.toHeaderString());
        }
    }
}
