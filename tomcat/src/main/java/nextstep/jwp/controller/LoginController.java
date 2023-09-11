package nextstep.jwp.controller;

import static common.ResponseStatus.FOUND;
import static common.ResponseStatus.OK;
import static org.apache.coyote.response.header.HttpHeader.SET_COOKIE;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String REDIRECT_URL = "/index.html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";
    private static final String URL = "/login";
    private static final String EXTENSION = ".html";

    private final AuthService authService;

    LoginController(AuthService authService) {
        this.authService = authService;
    }

    public LoginController() {
        this(new AuthService(SessionManager.getInstance(), InMemoryUserRepository.getInstance()));
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL);
    }


    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        doLogin(httpResponse, httpRequest.getBodyValue(ACCOUNT), httpRequest.getBodyValue(PASSWORD));
    }

    private void doLogin(HttpResponse httpResponse, String account, String password) {
        String sessionId = authService.login(account, password);
        httpResponse.setResponseRedirect(FOUND, REDIRECT_URL);
        httpResponse.setResponseHeader(SET_COOKIE, JSESSIONID_COOKIE + sessionId);
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
        String resourceUrl = toResourceUrl(httpRequest);
        ResourceManager manager = ResourceManager.from(resourceUrl);
        httpResponse.setResponseResource(
                OK,
                manager.extractResourceType(),
                manager.readResourceContent()
        );
    }

    private String toResourceUrl(HttpRequest httpRequest) {
        return httpRequest.requestUri() + EXTENSION;
    }
}
