package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.QueryParameter;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        if (hasNotQueryParameter(request)) {
            String location = request.getUri() + Constants.EXTENSION_OF_HTML;
            return HttpResponse.of(location, HttpStatusCode.OK);
        }

        try {
            QueryParameter queryParameter = request.getQueryParameter();
            String account = queryParameter.get(Constants.PARAMETER_KEY_OF_ACCOUNT);
            String password = queryParameter.get(Constants.PARAMETER_KEY_OF_PASSWORD);

            User user = findUserByAccountAndPassword(account, password);
            log.info("로그인 성공! 아이디 : {}", user.getAccount());
            return HttpResponse.of(Constants.DEFAULT_URI, HttpStatusCode.FOUND);
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
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 입력된 비밀번호: " + password);
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 아이디와 일치하는 유저를 찾을 수 없습니다. 입력된 아이디: " + account));
    }
}
