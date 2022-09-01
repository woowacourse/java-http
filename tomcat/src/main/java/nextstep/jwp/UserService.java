package nextstep.jwp;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public static final String URL_LOGIN = "/login";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_PASSWORD = "password";

    public static boolean process(final String url, final Map<String, String> params) {
        if (isLoginRequest(url, params)) {
            return UserService.login(params.get(KEY_ACCOUNT), params.get(KEY_PASSWORD));
        }
        return false;
    }

    private static boolean isLoginRequest(final String url, final Map<String, String> params) {
        return url.startsWith(URL_LOGIN) && !params.isEmpty();
    }

    private static boolean login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);
        return user.checkPassword(password);
    }

    public static User findByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);
    }
}
