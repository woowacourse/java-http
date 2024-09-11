package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String USER_FIELD = "user";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getValue(ACCOUNT_FIELD);
        String password = requestBody.getValue(PASSWORD_FIELD);
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));

        request.getSession().setAttribute(USER_FIELD, findUser);
        response.setBody(readStaticResource("/index.html"));
        response.setHeader(Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId());
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession();
        Object userSession = session.getAttribute(USER_FIELD);
        if (userSession == null) {
            response.setBody(readStaticResource("/login.html"));
            return;
        }
        response.setBody(readStaticResource("/index.html"));
        response.setHeader(Header.SET_COOKIE.value(), "JSESSIONID=" + request.getSession().getId());
    }
}
