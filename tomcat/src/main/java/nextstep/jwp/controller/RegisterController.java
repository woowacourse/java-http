package nextstep.jwp.controller;

import static common.ResponseStatus.FOUND;
import static common.ResponseStatus.OK;
import static org.apache.coyote.response.header.HttpHeader.SET_COOKIE;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.service.AuthService;
import org.apache.catalina.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REDIRECT_URL = "/index.html";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String URL = "/register";
    private static final String EXTENSION = ".html";

    private final AuthService authService;

    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    public RegisterController() {
        this(new AuthService(SessionManager.getInstance(), InMemoryUserRepository.getInstance()));
    }

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.consistsOf(URL);
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
