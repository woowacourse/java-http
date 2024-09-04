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

        User user = userRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀 번호 오류");
        }

        return redirectDefaultPage();
    }

    private HttpResponse redirectLoginPage() {
        return new HttpResponse(HttpStateCode.FOUND, "/login.html", MimeType.HTML);
    }

    private HttpResponse redirectDefaultPage() {
        return new HttpResponse(HttpStateCode.FOUND, "/index.html", MimeType.HTML);
    }
}
