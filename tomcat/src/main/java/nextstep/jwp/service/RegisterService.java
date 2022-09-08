package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.RegisterFailException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final String EMPTY_VALUE_ERROR_MESSAGE = "정보를 모두 입력해주세요.";
    private static final String DUPLICATED_ACCOUNT_ERROR_MESSAGE = "이미 존재하는 아이디입니다.";
    private static final String EMPTY_VALUE = "";

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

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

}
