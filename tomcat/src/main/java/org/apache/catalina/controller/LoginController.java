package org.apache.catalina.controller;

import static org.apache.catalina.controller.util.QueryParam.getQueryParams;
import static org.apache.catalina.controller.util.ResourceFinder.INDEX_RESOURCE_PATH;
import static org.apache.catalina.controller.util.ResourceFinder.UNAUTHORIZED_RESOURCE_PATH;
import static org.apache.catalina.controller.util.ResourceFinder.findResource;

import com.techcourse.model.User;
import com.techcourse.apiController.LoginApiController;
import com.techcourse.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestPath;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class LoginController extends AbstractController {

    private static final String LOGIN_PATH = "/login";
    public static final String USER = "user";
    public static final String DOT = ".";

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";

    private final LoginApiController loginApiController;

    public LoginController() {
        this.loginApiController = new LoginApiController(new UserService());
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return requestLine.isSame(LOGIN_PATH);
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.hasCookie()) {
            if (isLoggedInUser(httpRequest, httpResponse)) {
                return;
            }
        }

        RequestPath requestPath = httpRequest.getRequestPath();
        String resource = findResource(requestPath.getRequestPath() + DOT + ContentType.HTML);

        httpResponse.init(resource, ContentType.HTML, HttpStatus.OK);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String requestBody = httpRequest.getRequestBody().getBody();
        Map<String, String> bodyValues = getQueryParams(requestBody);

        try {
            User user = loginApiController.login(bodyValues.get(ACCOUNT), bodyValues.get(PASSWORD));

            setCookie(httpRequest, httpResponse, user);
            httpResponse.sendRedirect(INDEX_RESOURCE_PATH);
        } catch (IllegalArgumentException e) {
            httpResponse.init(findResource(UNAUTHORIZED_RESOURCE_PATH), ContentType.HTML, HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isLoggedInUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession(false);
        Optional<Object> user = session.getAttribute(USER);

        if (user.isPresent()) {
            httpResponse.sendRedirect(INDEX_RESOURCE_PATH);
            return true;
        }
        return false;
    }

    private void setCookie(final HttpRequest httpRequest, final HttpResponse httpResponse, final User user) {
        Session session = httpRequest.getSession(true);
        session.setAttribute(USER, user);

        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);

        httpResponse.setCookies(session.getId());
    }
}
