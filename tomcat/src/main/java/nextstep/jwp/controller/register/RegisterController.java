package nextstep.jwp.controller.register;

import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.handler.mapper.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Status;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.okWithResource("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        Map<String, String> params = httpRequest.getParams();

        validateRegisterValueIsNull(params.get(ACCOUNT), params.get(PASSWORD), params.get(EMAIL));
        userService.register(params.get(ACCOUNT), params.get(PASSWORD), params.get(EMAIL));

        return HttpResponse.responseWithResource(Status.FOUND, "/index.html");
    }

    private void validateRegisterValueIsNull(final String account, final String password, final String email) {
        if (account == null || password == null || email == null) {
            throw new BadRequestException("Account, Password, Email 값은 공백이 될 수 없습니다..");
        }
    }

}
