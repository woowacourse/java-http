package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class LoginController extends AbstractController {

    public static final String ACCOUNT_FIELD = "account";
    public static final String PASSWORD_FIELD = "password";

    @Override
    void doPost(HttpRequest request, HttpResponse response) throws Exception {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getValue(ACCOUNT_FIELD);
        String password = requestBody.getValue(PASSWORD_FIELD);
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
        request.getSession().setAttribute("user", findUser);
        response.setStatus(StatusCode.FOUND);
        response.setHeaders(Map.of(Header.LOCATION.value(), "/index.html",
                                   Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId()));
    }

    @Override
    void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResourceController.getInstance().handle(request, response, StatusCode.UNAUTHORIZED);
    }
}
