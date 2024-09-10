package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class LoginHandler implements Handler {

    public static final String ACCOUNT_FIELD = "account";
    public static final String PASSWORD_FIELD = "password";

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            login(request);
        } catch (IllegalArgumentException e) {
            return StaticResourceHandler.getInstance().handle(request, StatusCode.UNAUTHORIZED);
        }
        return new HttpResponse(
                StatusCode.FOUND,
                Map.of(Header.LOCATION.value(), "/index.html",
                       Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId()),
                null);
    }

    private void login(HttpRequest request) {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getValue(ACCOUNT_FIELD);
        String password = requestBody.getValue(PASSWORD_FIELD);
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
        request.getSession().setAttribute("user", findUser);
    }
}
