package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.ErrorMessage;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.renderer.StaticRenderer;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static com.techcourse.exception.ErrorMessage.ACCOUNT_NOT_FOUND;
import static org.apache.coyote.util.StringParser.parseQueryParameter;
import static org.apache.coyote.util.StringParser.parseQueryString;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager;
    private final StaticRenderer staticRenderer;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.staticRenderer = new StaticRenderer();
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        // 세션 로그인
        if (request.containsCookieKey("JSESSIONID")) {
            String jsessionid = request.getCookieValue("JSESSIONID");
            Session session = sessionManager.findSession(jsessionid);
            if (session != null) {
                log.info(session.getUser().toString());
                staticRenderer.redirectToIndexPage(response);
                return;
            }
            sessionManager.remove(jsessionid);
        }

        // 쿼리 파라미터 로그인
        String uri = request.getResourcePath();
        if (uri.contains("?")) {
            String queryString = parseQueryString(uri);
            if (login(parseQueryParameter(queryString), response)) {
                staticRenderer.redirectToIndexPage(response);
                return;
            }
            response.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
        }

        // 로그인 페이지 렌더링
        staticRenderer.renderStaticPage(request, response);
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        try {
            if (login(parseQueryParameter(request.getBody()), response)) {
                staticRenderer.redirectToIndexPage(response);
            }
            response.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
        } catch (Exception e){
            response.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
            staticRenderer.renderStaticPage(request, response);
        }
    }

    private boolean login(Map<String, String> params, Response response) throws IllegalArgumentException{
        String account = params.get("account");
        String password = params.get("password");
        if (account == null || password == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_LOGIN_REQUEST.getMessage());
        }
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND.getMessage()));
        if (user.checkPassword(password)) {
            String sessionId = UUID.randomUUID().toString();
            Session session = new Session(sessionId);
            session.setAttribute("user", user);
            sessionManager.addSession(sessionId, session);
            response.addCookie("JSESSIONID", sessionId);
            return true;
        }
        return false;
    }

}
