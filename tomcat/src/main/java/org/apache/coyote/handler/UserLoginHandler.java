package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.MethodType;
import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.util.HeaderParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(UserLoginHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public String handle(final HttpRequest httpRequest) {
        if (httpRequest.method().equals(MethodType.GET.getMethod())) {
            return handleLoginGet(httpRequest.version(),httpRequest.cookie());
        }

        if (httpRequest.method().equals(MethodType.POST.getMethod())) {
            return handleLoginPost(httpRequest.version(),httpRequest.queryParams(), httpRequest.cookie());
        }

        throw new IllegalArgumentException("정의되지 않는 Method 입니다.");
    }

    private String handleLoginGet(String version,HttpCookie httpCookie) {
        if (httpCookie.hasJSESSIONID()) {
            try {
                Session session = sessionManager.findSession(httpCookie.getSessionId());
                if (session != null && session.getAttribute("user") != null) {
                    return PageRenderer.sendRedirect(version, HttpStatus.FOUND.getStatusCode(),
                            HeaderParser.createRedirectHeaders("/", httpCookie, session.getId()));
                }

            } catch (IOException e) {
                log.warn("Invalid session: {}", httpCookie.getSessionId(), e);
            }
        }

        return PageRenderer.createStaticFileResponse(version,HttpStatus.OK.getStatusCode(),"/login.html");
    }

    private String handleLoginPost(String version, Map<String, String> queryParams, HttpCookie httpCookie) {
        User user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT))
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        if (!checkUserPassword(user, queryParams.get(PASSWORD))) {
            return PageRenderer.sendRedirect(version, HttpStatus.FOUND.getStatusCode(),
                    HeaderParser.createRedirectHeaders("/401.html", null, null));
        }

        String sessionId = sessionManager.generateJSESSIONID();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        return PageRenderer.sendRedirect(version, HttpStatus.FOUND.getStatusCode(),
                HeaderParser.createRedirectHeaders("/", httpCookie, sessionId));
    }

    private boolean checkUserPassword(final User user, final String inputPassword) {
        return user.checkPassword(inputPassword);
    }
}
