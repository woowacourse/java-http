package nextstep.jwp.db;


import nextstep.jwp.exception.DuplicateException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<String, User> database;

    public InMemoryUserRepository() {
        database = new ConcurrentHashMap<>();
        saveInitUserData();
    }

    private void saveInitUserData() {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public void save(User user) {
        validateNotDuplicate(user);
        database.put(user.getAccount(), user);
    }

    private void validateNotDuplicate(User user) {
        validateAccountNotDuplicate(user.getAccount());
        validateEmailNotDuplicate(user.getEmail());
    }

    private void validateAccountNotDuplicate(String account) {
        if (database.containsKey(account)) {
            LOG.debug("회원 등록 실패 : account 중복 => account: {}", account);
            throw new DuplicateException("이미 존재하는 account 입니다.");
        }
    }

    private void validateEmailNotDuplicate(String email) {
        if (isEmailAlreadyExists(email)) {
            LOG.debug("회원 등록 실패 : email 중복 => email: {}", email);
            throw new DuplicateException("이미 존재하는 email 입니다.");
        }
    }

    private boolean isEmailAlreadyExists(String email) {
        return database.values().stream()
                .anyMatch(userInDB -> userInDB.hasSameEmail(email));
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
