package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String EXTENSION_OF_HTML = ".html";
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String PARAMETER_KEY_OF_ACCOUNT = "account";
    private static final String PARAMETER_KEY_OF_PASSWORD = "password";

    @Override
    public HttpResponse process(String uri) {
        int index = uri.indexOf(QUERY_STRING_DELIMITER);
        if (index == -1) {
            return HttpResponse.of("static" + uri + EXTENSION_OF_HTML, HttpStatusCode.OK);
        }

        try {
            User user = findUser(uri.substring(index + 1));
            log.info("로그인 성공! 아이디 : {}", user.getAccount());
            return HttpResponse.of("static" + "/index.html", HttpStatusCode.FOUND);
        } catch (IllegalArgumentException e) {
            log.info("로그인 실패 : {}", e.getMessage(), e);
            return HttpResponse.of("static" + "/401.html", HttpStatusCode.FOUND);
        }
    }

    private User findUser(String queryString) {
        String account = findAccount(queryString);
        String password = findPassword(queryString);

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

    private String findAccount(String queryString) {
        return Arrays.stream(queryString.split(PARAMETER_DELIMITER))
                .filter(parameter -> parameter.startsWith(PARAMETER_KEY_OF_ACCOUNT))
                .findFirst()
                .map(this::getAccount)
                .orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 값을 찾을 수 없습니다."));
    }

    private String findPassword(String queryString) {
        return Arrays.stream(queryString.split(PARAMETER_DELIMITER))
                .filter(parameter -> parameter.startsWith(PARAMETER_KEY_OF_PASSWORD))
                .findFirst()
                .map(this::getPassword)
                .orElseThrow(() -> new IllegalArgumentException("비밀번호에 해당하는 값을 찾을 수 없습니다."));
    }

    private String getAccount(String param) {
        return param.split(KEY_VALUE_DELIMITER)[1];
    }

    private String getPassword(String param) {
        return param.split(KEY_VALUE_DELIMITER)[1];
    }
}
