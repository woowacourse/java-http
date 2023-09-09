package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.line.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;
import static org.apache.coyote.http11.response.line.ResponseStatus.UNAUTHORIZED;

import nextstep.jwp.service.AuthService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String REDIRECT_URL = "/index.html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";
    private static final String UNAUTHORIZED_URL = "/401.html";
    private static final String RESOURCE_URL = "/login.html";

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(POST, "/login")) {
            doPost(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(GET, "/login")) {
            doGet(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        doLogin(httpResponse, httpRequest.getBodyValue(ACCOUNT), httpRequest.getBodyValue(PASSWORD));
    }

    private void doLogin(HttpResponse httpResponse, String account, String password) {
        try {
            String sessionId = authService.login(account, password);
            httpResponse.setResponseRedirect(FOUND, REDIRECT_URL);
            httpResponse.setResponseHeader(SET_COOKIE, JSESSIONID_COOKIE + sessionId);
        } catch (IllegalArgumentException e) {
            httpResponse.setResponseResource(UNAUTHORIZED, UNAUTHORIZED_URL);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.hasQueryString()) {
            doLogin(httpResponse, httpRequest.getQueryStringValue(ACCOUNT), httpRequest.getQueryStringValue(PASSWORD));
            return;
        }
        if (httpRequest.hasSessionId() & authService.isLoggedIn(httpRequest.sessionId())) {
            httpResponse.setResponseRedirect(FOUND, REDIRECT_URL);
            return;
        }
        httpResponse.setResponseResource(OK, RESOURCE_URL);
    }
}
