package nextstep.jwp.controller;

import nextstep.jwp.dto.UserDto;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private final AuthService authService = AuthService.getInstance();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.getRequestBody();
        final UserDto requestDto = new UserDto(requestBody.get("account"), requestBody.get("password"),
                requestBody.get("email"));

        final String sessionId = authService.register(requestDto);
        return HttpResponse.init(HttpStatusCode.FOUND)
                .setLocationAsHome()
                .addCookie("JSESSIONID", sessionId);
    }
}
