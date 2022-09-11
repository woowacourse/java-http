package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.REGISTER_HTML;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import nextstep.jwp.application.UserService;
import nextstep.jwp.dto.UserRegisterRequest;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterController extends ResourceController {

    private final UserService userService = UserService.getInstance();

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet();
        }
        return doPost(httpRequest);
    }

    private HttpResponse doGet() {
        return generateResourceResponse(REGISTER_HTML);
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();

        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        validateRegisterParams(queryParams);

        final String account = queryParams.get("account");
        final String email = queryParams.get("email");
        final String password = queryParams.get("password");
        final UserRegisterRequest userRegisterRequest = new UserRegisterRequest(account, password, email);
        userService.save(userRegisterRequest);

        final HttpHeader location = HttpHeader.of(LOCATION.getValue(), INDEX_HTML);
        return HttpResponse.of(HTTP11, REDIRECT, location);
    }

    private void validateRegisterParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account")
                || !queryParams.containsKey("password")
                || !queryParams.containsKey("email")) {
            throw new IllegalArgumentException("회원가입 정보가 입력되지 않았습니다.");
        }
    }
}
