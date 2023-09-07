package org.apache.coyote.http11;

import java.util.Objects;
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
    private final HttpResponse httpResponse;

    public RequestHandler(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public HttpResponse execute() {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            if (httpRequest.isSameParsedRequestURI("/login")) {
                return branchOfLoginRequest();
            }
            if (httpRequest.isSameParsedRequestURI("/register")) {
                httpResponse.updateRedirect(httpRequest.getHttpVersion(), httpRequest.getParsedRequestURI());
                return httpResponse;
            }
            if (httpRequest.isSameParsedRequestURI(HOME_PAGE)) {
                httpResponse.updatePage(HOME_PAGE);
                return httpResponse;
            }
        }

        if (httpRequest.isSameHttpMethod(HttpMethod.POST)) {
            if (httpRequest.isSameParsedRequestURI("/login")) {
                return login();
            }
            if (httpRequest.isSameParsedRequestURI("/register")) {
                return branchOfRegisterRequest();
            }
        }

        return httpResponse;
    }

    private HttpResponse branchOfRegisterRequest() {
        final RequestBody requestBody = httpRequest.getRequestBody();
        SignupManager.singUp(requestBody);
        httpResponse.updateRedirect(httpRequest.getHttpVersion(), HOME_PAGE);
        return httpResponse;
    }

    private HttpResponse branchOfLoginRequest() {
        if (alreadyLoginStatus()) {
            return httpResponse;
        }
        httpResponse.updatePage("/login");
        return httpResponse;
    }

    private HttpResponse login() {
        if (alreadyLoginStatus()) {
            return httpResponse;
        }
        // 요청에 JSESSIONID 가 없다
        // 새로운 세션을 생성해서 넣어준다.
        final Session newSession = new Session();
        if (LoginValidator.check(httpRequest, newSession)) {
            httpResponse.updateRedirect(httpRequest.getHttpVersion(), HOME_PAGE);
            httpResponse.addHeader("Set-Cookie", SESSION_ID + "=" + newSession.getId());
            return httpResponse;
        }

        httpResponse.updateRedirect(httpRequest.getHttpVersion(), UNAUTHORIZED_PAGE);
        return httpResponse;
    }

    private boolean alreadyLoginStatus() {
        // 애초에
        // 1. JSESSIONID 가 없거나
        // 2. request 에 JESSIONID 는 있지만 세션이 없거나?
        // 3. 세션은 찾았지만 user 가 들어있지 않거나 -> login 페이지로
        if (httpRequest.hasCookie(SESSION_ID)) {
            final String jSessionId = httpRequest.getCookieValue(SESSION_ID);
            final Session session = SESSION_MANAGER.findSession(jSessionId);
            if (Objects.nonNull(session) && Objects.nonNull(session.getAttribute("user"))) {
                httpResponse.updateRedirect(httpRequest.getHttpVersion(), HOME_PAGE);
                return true;
            }
        }
        return false;
    }
}
