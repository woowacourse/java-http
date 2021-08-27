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
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        validateNotDuplicate(user);
        database.put(user.getAccount(), user);
    }

    private static void validateNotDuplicate(User user) {
        validateAccountNotDuplicate(user.getAccount());
        validateEmailNotDuplicate(user.getEmail());
    }

    private static void validateAccountNotDuplicate(String account) {
        if (database.containsKey(account)) {
            LOG.debug("회원 등록 실패 : account 중복 => account: {}", account);
            throw new DuplicateException("이미 존재하는 account 입니다.");
        }
    }

    private static void validateEmailNotDuplicate(String email) {
        if (isEmailAlreadyExists(email)) {
            LOG.debug("회원 등록 실패 : email 중복 => email: {}", email);
            throw new DuplicateException("이미 존재하는 email 입니다.");
        }
    }

    private static boolean isEmailAlreadyExists(String email) {
        return database.values().stream()
                .anyMatch(userInDB -> userInDB.hasSameEmail(email));
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
