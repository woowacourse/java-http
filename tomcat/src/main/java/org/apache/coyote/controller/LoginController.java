package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStateCode;
import org.apache.coyote.http11.MimeType;

public class LoginController implements Controller {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final InMemoryUserRepository userRepository;

    public LoginController() {
        userRepository = new InMemoryUserRepository();
    }

    @Override
    public HttpResponse run(HttpRequest request) {

        Map<String, String> queryMap = request.getQueryMap();
        if (!queryMap.containsKey(ACCOUNT_KEY) || !queryMap.containsKey(PASSWORD_KEY)) {
            return redirectLoginPage();
        }

        String account = queryMap.get(ACCOUNT_KEY);
        String password = queryMap.get(PASSWORD_KEY);

        try {
            login(account, password);
        } catch (IllegalArgumentException e) {
            return redirectUnauthorizedPage();
        }

        return redirectDefaultPage();
    }

    private void login(String account, String password) {
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호 오류가 발생했습니다.");
        }
    }

    private HttpResponse redirectLoginPage() {
        return new HttpResponse(HttpStateCode.FOUND, "/login.html", MimeType.HTML);
    }

    private HttpResponse redirectDefaultPage() {
        return new HttpResponse(HttpStateCode.FOUND, "/index.html", MimeType.HTML);
    }

    private HttpResponse redirectUnauthorizedPage() {
        return new HttpResponse(HttpStateCode.FOUND, "/401.html", MimeType.HTML);
    }
}
