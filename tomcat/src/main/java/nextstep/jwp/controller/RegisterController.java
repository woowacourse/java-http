package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class RegisterController extends AbstractController {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        return HttpResponse.ofResource("/register.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final QueryParams requestParams = httpRequest.getBodyQueryParams();
        checkParams(requestParams);
        UserService.register(requestParams.get(KEY_ACCOUNT), requestParams.get(KEY_PASSWORD),
                requestParams.get(KEY_EMAIL));
        return HttpResponse.ofRedirection(StatusCode.SEE_OTHER, "/index.html");
    }

    private void checkParams(final QueryParams queryParams) {
        if (!queryParams.containsKey(KEY_ACCOUNT)
                || !queryParams.containsKey(KEY_PASSWORD)
                || !queryParams.containsKey(KEY_EMAIL)) {
            throw new IllegalArgumentException("아이디와 비밀번호, 이메일을 모두 입력하세요.");
        }
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isPath("/register");
    }
}
