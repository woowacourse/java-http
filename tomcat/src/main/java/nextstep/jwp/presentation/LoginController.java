package nextstep.jwp.presentation;

import http.HttpHeaders;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpStatus;
import java.util.UUID;
import nextstep.jwp.application.AuthService;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String REDIRECT_PAGE_PATH = "/index.html";

    private final AuthService authService;

    private LoginController(final AuthService authService) {
        this.authService = authService;
    }

    public static LoginController instance() {
        return LoginControllerHolder.instance;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        if (isLoggedIn(httpRequest)) {
            return redirect(REDIRECT_PAGE_PATH);
        }

        String responseBody = readResource(httpRequest);
        HttpHeaders responseHeaders = setResponseHeaders(httpRequest, responseBody);

        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        HttpHeaders responseHeaders = HttpHeaders.createEmpty();
        responseHeaders.add("Location", REDIRECT_PAGE_PATH);

        User loginUser = authService.login(httpRequest.getBody());
        Session session = httpRequest.getSession();
        if (session == null) {
            session = new Session(UUID.randomUUID().toString());
            SessionManager.instance().add(session);
            responseHeaders.add("Set-Cookie", "JSESSIONID=" + session.getId());
        }
        session.setAttribute("user", loginUser);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    private boolean isLoggedIn(final HttpRequest httpRequest) {
        Session session = httpRequest.getSession();
        if (session == null) {
            return false;
        }
        User loginUser = (User) session.getAttribute("user");
        return loginUser != null;
    }

    public static class LoginControllerHolder {

        private static final LoginController instance = new LoginController(AuthService.instance());
    }
}
