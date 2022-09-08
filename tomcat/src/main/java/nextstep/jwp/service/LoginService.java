package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final String EMPTY_VALUE_ERROR_MESSAGE = "정보를 모두 입력해주세요.";
    private static final String WRONG_PASSWORD_ERROR_MESSAGE = "비밀번호가 맞지 않습니다.";
    private static final String NOT_FOUND_USER_ERROR_MESSAGE = "유저를 찾을 수 없습니다.";
    private static final String EMPTY_VALUE = "";

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void login(Map<String, String> requestBody) {
        String account = requestBody.getOrDefault("account", EMPTY_VALUE);
        String password = requestBody.getOrDefault("password", EMPTY_VALUE);
        checkEmptyInput(account, password);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_ERROR_MESSAGE));
        checkValidPassword(user, password);
    }

    private void checkEmptyInput(String account, String password) {
        if (account.isBlank() || password.isBlank()) {
            throw new LoginFailException(EMPTY_VALUE_ERROR_MESSAGE);
        }
    }

    private void checkValidPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            log.warn(WRONG_PASSWORD_ERROR_MESSAGE);
            throw new LoginFailException(WRONG_PASSWORD_ERROR_MESSAGE);
        }
    }
}
