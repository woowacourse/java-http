package nextstep.jwp.controller;

import static common.ResponseStatus.FOUND;
import static common.ResponseStatus.OK;
import static common.ResponseStatus.UNAUTHORIZED;
import static org.apache.coyote.request.line.HttpMethod.GET;
import static org.apache.coyote.request.line.HttpMethod.POST;
import static org.apache.coyote.response.header.HttpHeader.SET_COOKIE;

import nextstep.jwp.service.AuthService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String REDIRECT_URL = "/index.html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";
    private static final String UNAUTHORIZED_URL = "/401.html";
    private static final String RESOURCE_URL = "/login.html";
    private static final String URL = "/login";

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL);
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedOperationException();
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
        if (httpRequest.hasSessionId() && authService.isLoggedIn(httpRequest.sessionId())) {
            httpResponse.setResponseRedirect(FOUND, REDIRECT_URL);
            return;
        }
        httpResponse.setResponseResource(OK, RESOURCE_URL);
    }
}
