package org.apache.coyote.http11;

import nextstep.jwp.model.LoginValidator;
import nextstep.jwp.model.SignupManager;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class RequestHandler {

    public static final SessionManager SESSION_MANAGER = new SessionManager();
    public static final String HOME_PAGE = "/index.html";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String SESSION_ID = "JSESSIONID";

    private final HttpRequest httpRequest;

    public RequestHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String execute() {
        if (httpRequest.getHttpMethod() == HttpMethod.GET && (httpRequest.isSameParsedRequestURI("/login")
                && httpRequest.hasCookie(SESSION_ID))) {
            return ResponseBody.redirectResponse(HOME_PAGE, httpRequest.getHttpVersion());
        }

        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            if (httpRequest.isSameParsedRequestURI("/login")) {
                return branchOfLoginRequest();
            }
            if (httpRequest.isSameParsedRequestURI("/register")) {
                return branchOfRegisterRequest();
            }
        }

        return ResponseBody.from(httpRequest.getParsedRequestURI(), HttpStatus.OK, httpRequest.getHttpVersion())
                .getMessage();
    }

    private Session findSession() {
        if (httpRequest.hasCookie(SESSION_ID)) {
            final String jsessionid = httpRequest.getCookieValue(SESSION_ID);
            return SESSION_MANAGER.findSession(jsessionid);
        }
        final Session session = new Session();
        SESSION_MANAGER.add(session);
        return session;
    }

    private String branchOfRegisterRequest() {
        final RequestBody requestBody = httpRequest.getRequestBody();
        SignupManager.singUp(requestBody);
        return ResponseBody.redirectResponse(HOME_PAGE, httpRequest.getHttpVersion());
    }

    private String branchOfLoginRequest() {
        final Session session = findSession();
        if (httpRequest.hasCookie(SESSION_ID)) {
            return ResponseBody.redirectResponse(HOME_PAGE, httpRequest, session.getId());
        }
        return login(session.getId());
    }

    private String login(final String sessionId) {
        if (LoginValidator.check(httpRequest, sessionId)) {
            return ResponseBody.redirectResponse(HOME_PAGE, httpRequest, sessionId);
        }
        return ResponseBody.redirectResponse(UNAUTHORIZED_PAGE, httpRequest.getHttpVersion());
    }
}
