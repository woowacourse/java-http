package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpHeader.SET_COOKIE;
import static org.apache.coyote.http11.response.line.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.line.ResponseStatus.OK;

import nextstep.jwp.service.AuthService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_URL = "/index.html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String RESOURCE_URL = "/register.html";
    private final AuthService authService;

    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.consistsOf(POST, "/register")) {
            doPost(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.consistsOf(GET, "/register")) {
            doGet(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = authService.register(
                httpRequest.getBodyValue(ACCOUNT),
                httpRequest.getBodyValue(PASSWORD),
                httpRequest.getBodyValue(EMAIL)
        );
        httpResponse.setResponseRedirect(FOUND, REDIRECT_URL);
        httpResponse.setResponseHeader(SET_COOKIE, JSESSIONID_COOKIE + sessionId);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setResponseResource(OK, RESOURCE_URL);
    }
}
