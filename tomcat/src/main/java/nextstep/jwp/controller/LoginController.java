package nextstep.jwp.controller;

import nextstep.jwp.exception.UnsupportedMethodException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.message.request.QueryParams;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.requestline.Method;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.header.StatusCode;

public class LoginController implements Controller {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_PASSWORD = "password";

    @Override
    public Response service(final Request request) throws Exception {
        if (request.isMethod(Method.GET)) {
            return Response.ofResource("/login.html");
        }

        if (request.isMethod(Method.POST)) {
            return doPost(request);
        }

        throw new UnsupportedMethodException();
    }

    private static Response doPost(final Request request) {
        final QueryParams requestParams = request.getBodyQueryParams();
        checkParams(requestParams);

        UserService.login(requestParams.get(KEY_ACCOUNT), requestParams.get(KEY_PASSWORD));
        return Response.ofRedirection(StatusCode.FOUND, "/index.html");
    }

    private static void checkParams(final QueryParams queryParams) {
        if (!queryParams.containsKey(KEY_ACCOUNT) || !queryParams.containsKey(KEY_PASSWORD)) {
            throw new IllegalArgumentException("계정과 비밀번호를 입력하세요.");
        }
    }

    @Override
    public boolean canHandle(final Request request) {
        return request.isPath("/login");
    }
}
