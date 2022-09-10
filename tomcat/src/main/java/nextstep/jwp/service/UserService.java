package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.RegisterFailException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String EMPTY_VALUE_ERROR_MESSAGE = "정보를 모두 입력해주세요.";
    private static final String WRONG_PASSWORD_ERROR_MESSAGE = "비밀번호가 맞지 않습니다.";
    private static final String NOT_FOUND_USER_ERROR_MESSAGE = "유저를 찾을 수 없습니다.";
    private static final String DUPLICATED_ACCOUNT_ERROR_MESSAGE = "이미 존재하는 아이디입니다.";
    private static final String EMPTY_VALUE = "";

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    public void login(Map<String, String> requestBody) {
        String account = requestBody.getOrDefault(ACCOUNT, EMPTY_VALUE);
        String password = requestBody.getOrDefault(PASSWORD, EMPTY_VALUE);
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

    public void register(Map<String, String> requestBody) {
        String account = requestBody.getOrDefault("account", EMPTY_VALUE);
        String email = requestBody.getOrDefault("email", EMPTY_VALUE);
        String password = requestBody.getOrDefault("password", EMPTY_VALUE);
        checkEmptyInput(account, email, password);

        checkDuplicateAccount(account);
        InMemoryUserRepository.save(new User(account, password, email));
    }

    private void checkEmptyInput(String account, String email, String password) {
        if (account.isBlank() || email.isBlank() || password.isBlank()) {
            throw new RegisterFailException(EMPTY_VALUE_ERROR_MESSAGE);
        }
    }

    private void checkDuplicateAccount(String account) {
        boolean isExistAccount = InMemoryUserRepository.existsByAccount(account);
        if (isExistAccount) {
            log.error(DUPLICATED_ACCOUNT_ERROR_MESSAGE);
            throw new RegisterFailException(DUPLICATED_ACCOUNT_ERROR_MESSAGE);
        }
    }

    public User findUser(Map<String, String> requestBody) {
        String account = requestBody.getOrDefault(ACCOUNT, EMPTY_VALUE);

        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_ERROR_MESSAGE));
    }

    public User getUser(Session session) {
        return (User) session.getAttribute(USER);
    }

    public boolean isValidUser(User user) {
        return InMemoryUserRepository.isValidUser(user);
    }
}
