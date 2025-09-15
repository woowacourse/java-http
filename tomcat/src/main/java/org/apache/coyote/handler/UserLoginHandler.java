package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.dto.HttpResponse;
import org.apache.coyote.render.HttpStatus;
import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.util.HeaderParser;

public class UserLoginHandler extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private final SessionManager sessionManager;

    private static final UserLoginHandler INSTANCE = new UserLoginHandler(SessionManager.getInstance());

    private UserLoginHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static UserLoginHandler getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response){
        HttpCookie httpCookie = request.getCookie();
        if (httpCookie.hasJSESSIONID()) {
            Session session = sessionManager.findSession(httpCookie.getSessionId());
            if (session != null && session.getAttribute("user") != null) {
                PageRenderer.sendRedirect(
                        request.version(),
                        HttpStatus.FOUND.getStatusCode(),
                        HeaderParser.createRedirectHeaders("/", httpCookie, session.getId()),
                        response
                );
                return;
            }
        }
        PageRenderer.createStaticFileResponse(request.version(), HttpStatus.OK.getStatusCode(), "/login.html",
                response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> queryParams = request.queryParams();
        String version = request.version();
        HttpCookie httpCookie = request.getCookie();
        User user = InMemoryUserRepository.findByAccount(queryParams.get(ACCOUNT))
                .orElse(null);
        if (user == null || !checkUserPassword(user, queryParams.get(PASSWORD))) {
            PageRenderer.sendRedirect(
                    version,
                    HttpStatus.FOUND.getStatusCode(),
                    HeaderParser.createRedirectHeaders("/401.html", null, null),
                    response
            );
            return;
        }

        String sessionId = sessionManager.generateJSESSIONID();
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);

        PageRenderer.sendRedirect(
                version,
                HttpStatus.FOUND.getStatusCode(),
                HeaderParser.createRedirectHeaders("/", httpCookie, sessionId),
                response
        );
    }

    private boolean checkUserPassword(final User user, final String inputPassword) {
        return user.checkPassword(inputPassword);
    }
}
