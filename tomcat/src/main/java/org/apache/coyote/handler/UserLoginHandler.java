package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.PageRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginHandler implements RequestHandler{

    private static final Logger log = LoggerFactory.getLogger(UserLoginHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String GET_METHOD_REQUEST = "GET";
    private static final String POST_METHOD_REQUEST = "POST";

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public String handle(final String method, final String path, final Map<String,String> queryParams, final HttpCookie httpCookie
    ) {
        if (method.equals(GET_METHOD_REQUEST)) {
            return handleLoginGet(httpCookie);
        }
        if (method.equals(POST_METHOD_REQUEST)) {
            return handleLoginPost(queryParams, httpCookie);
        }
        throw new IllegalArgumentException("정의되지 않는 Method 입니다.");
    }

    private String handleLoginGet(HttpCookie httpCookie) {
        if (httpCookie.hasJSESSIONID()) {
            try {
                Session session = sessionManager.findSession(httpCookie.getSessionId());
                if (session != null && session.getAttribute("user") != null) {
                    return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/", httpCookie.getSessionId());
                }
            } catch (IOException e) {
                log.warn("Invalid session: {}", httpCookie.getSessionId(), e);
            }
        }
        return PageRenderer.createStaticFileResponse(HttpStatus.OK.getStatusCode(), "/login.html");
    }

    private String handleLoginPost(Map<String, String> queryParams, HttpCookie httpCookie) {
        try {
            User user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT))
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

            checkUserPassword(user, queryParams.get(PASSWORD));
            String sessionId = httpCookie.generateJSESSIONID();
            Session session = new Session(sessionId);
            session.setAttribute("user",user);
            sessionManager.add(session);

            return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/", sessionId);
        } catch (IllegalArgumentException e) {
            return PageRenderer.sendRedirect(HttpStatus.FOUND.getStatusCode(), "/401.html", null);
        }
    }

    private void checkUserPassword(final User user, final String inputPassword) {
        if (!user.checkPassword(inputPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }


}
