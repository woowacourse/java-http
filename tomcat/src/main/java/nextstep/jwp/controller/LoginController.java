package nextstep.jwp.controller;

import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.exception.WrongPasswordException;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeader;
import org.apache.coyote.http.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final LoginController INSTANCE = new LoginController();

    private final AuthService authService = AuthService.getInstance();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (alreadyLogin(httpRequest.getHeader())) {
            return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.FOUND)
                    .setLocationAsHome();
        }
        return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.OK)
                .setBodyByPath("/login.html");
    }

    public boolean alreadyLogin(final RequestHeader header) {
        if (!header.hasSessionId()) {
            return false;
        }
        return authService.alreadyLogin(header.getSessionId());
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.getRequestBody();
        final LoginRequest requestDto = new LoginRequest(requestBody.get("account"), requestBody.get("password"));

        try {
            final String sessionId = authService.login(requestDto);
            return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.FOUND)
                    .setLocationAsHome()
                    .addCookie("JSESSIONID", sessionId);
        } catch (final UserNotFoundException e) {
            return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.NOT_FOUND)
                    .setBodyByPath("/404.html");
        } catch (final WrongPasswordException e) {
            return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.UNAUTHORIZED)
                    .setBodyByPath("/401.html");
        }
    }
}
