package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.common.Request;
import org.apache.coyote.common.RequestBody;
import org.apache.coyote.common.Response;
import org.apache.coyote.common.StatusCode;

public class LoginHandler implements Handler {

    public static final String ACCOUNT_FIELD = "account";
    public static final String PASSWORD_FIELD = "password";

    @Override
    public Response handle(Request request) {
        if (isInvalidRequestBody(request)) {
            return StaticResourceHandler.getInstance().handle(request);
        }
        try {
            login(request);
        } catch (IllegalArgumentException e) {
            return StaticResourceHandler.getInstance().handle(request, StatusCode.UNAUTHORIZED);
        }
        return new Response(
                StatusCode.FOUND,
                Map.of("Location", "/index.html",
                       "Set-Cookie", "JSESSIONID=" + request.getSession().getId()),
                null);
    }

    private boolean isInvalidRequestBody(Request request) {
        RequestBody requestBody = request.getBody();
        return requestBody.missing(ACCOUNT_FIELD) || requestBody.missing(PASSWORD_FIELD);
    }

    private void login(Request request) {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getValue(ACCOUNT_FIELD);
        String password = requestBody.getValue(PASSWORD_FIELD);
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
        request.getSession().setAttribute("user", findUser);
    }
}
