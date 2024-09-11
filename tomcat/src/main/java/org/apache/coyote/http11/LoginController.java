package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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

    @Override
    public String process(String uri) {
        int index = uri.indexOf(QUERY_STRING_DELIMITER);
        if (index == -1) {
            return uri + EXTENSION_OF_HTML;
        }

        String queryString = uri.substring(index + 1);
        User user = findUser(queryString);
        log.info("user : {}", user);

        return uri.substring(0, index) + EXTENSION_OF_HTML;
    }

    private User findUser(String queryString) {
        String account = findAccount(queryString);
        return findUserByAccount(account);
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("입력된 정보와 일치하는 유저를 찾을 수 없습니다."));
    }

    private String findAccount(String queryString) {
        return Arrays.stream(queryString.split(PARAMETER_DELIMITER))
                .filter(parameter -> parameter.startsWith(PARAMETER_KEY_OF_ACCOUNT))
                .findFirst()
                .map(this::getAccountValue)
                .orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 값을 찾을 수 없습니다."));
    }

    private String getAccountValue(String param) {
        return param.split(KEY_VALUE_DELIMITER)[1];
    }
}
