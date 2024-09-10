package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.QueryParameter;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String EXTENSION_OF_HTML = ".html";
    private static final String PARAMETER_KEY_OF_ACCOUNT = "account";
    private static final String PARAMETER_KEY_OF_PASSWORD = "password";

    @Override
    public HttpResponse process(HttpRequest request) {
        if (hasNotQueryParameter(request)) {
            return HttpResponse.of(request.getUri() + EXTENSION_OF_HTML, HttpStatusCode.OK);
        }

        QueryParameter queryParameter = request.getQueryParameter();
        String account = queryParameter.get(PARAMETER_KEY_OF_ACCOUNT);
        String password = queryParameter.get(PARAMETER_KEY_OF_PASSWORD);

        try {
            User user = findUserByAccountAndPassword(account, password);
            log.info("로그인 성공! 아이디 : {}", user.getAccount());
            return HttpResponse.of("/index.html", HttpStatusCode.FOUND);
        } catch (IllegalArgumentException e) {
            log.info("로그인 실패 : {}", e.getMessage(), e);
            return HttpResponse.of("/401.html", HttpStatusCode.FOUND);
        }
    }

    private boolean hasNotQueryParameter(HttpRequest request) {
        return !request.hasQueryParameter();
    }

    private User findUserByAccountAndPassword(String account, String password) {
        User user = findUserByAccount(account);
        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 아이디와 일치하는 유저를 찾을 수 없습니다."));
    }
}
