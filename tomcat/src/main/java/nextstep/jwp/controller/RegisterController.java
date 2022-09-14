package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.REGISTER_HTML;

import java.util.Map;
import nextstep.jwp.application.UserService;
import nextstep.jwp.dto.UserRegisterRequest;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterController extends ResourceController {

    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(final HttpRequest httpRequest,
                         final HttpResponse httpResponse) {
        setResource(REGISTER_HTML.getValue(), httpResponse);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest,
                          final HttpResponse httpResponse) {
        final String body = httpRequest.getBody();

        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        validateRegisterParams(queryParams);

        final String account = queryParams.get("account");
        final String email = queryParams.get("email");
        final String password = queryParams.get("password");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(account, password, email);
        userService.save(userRegisterRequest);
        setRedirectHeader(httpResponse, INDEX_HTML);
    }

    private void validateRegisterParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")
                || !queryParams.containsKey("email")) {
            throw new IllegalArgumentException("회원가입 정보가 입력되지 않았습니다.");
        }
    }
}
