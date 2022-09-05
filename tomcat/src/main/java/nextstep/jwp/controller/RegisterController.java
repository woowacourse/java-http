package nextstep.jwp.controller;

import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class RegisterController implements Controller {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    private static Response doPost(final Request request) {
        final QueryParams requestParams = request.getBodyQueryParams();
        checkParams(requestParams);
        UserService.register(requestParams.get(KEY_ACCOUNT), requestParams.get(KEY_PASSWORD),
                requestParams.get(KEY_EMAIL));
        return Response.ofRedirection(StatusCode.SEE_OTHER, "/index.html");
    }

    private static void checkParams(final QueryParams queryParams) {
        if (!queryParams.containsKey(KEY_ACCOUNT)
                || !queryParams.containsKey(KEY_PASSWORD)
                || !queryParams.containsKey(KEY_EMAIL)) {
            throw new IllegalArgumentException("아이디와 비밀번호, 이메일을 모두 입력하세요.");
        }
    }

    @Override
    public Response service(final Request request) throws Exception {

        if (request.isMethod(Method.GET)) {
            return Response.ofResource("/register.html");
        }

        if (request.isMethod(Method.POST)) {
            return doPost(request);
        }

        throw new UnsupportedMethodException();
    }

    @Override
    public boolean canHandle(final Request request) {
        return request.isPath("/register");
    }
}
