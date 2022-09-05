package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParams;

public class LoginService {

    private LoginService() {
    }

    private static class LoginServiceHolder {
        private static final LoginService instance = new LoginService();
    }

    public static LoginService instance() {
        return LoginServiceHolder.instance;
    }

    public String login(final String requestBody) {
        QueryParams queryParams = QueryParams.parseQueryParams(requestBody);
        validateLogin(queryParams);
        return "/index.html";
    }

    private void validateLogin(final QueryParams queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        validateLoginFormat(account, password);
        User user = findUser(account);
        checkPassword(password, user);
    }

    private void validateLoginFormat(final String account, final String password) {
        if (account == null || password == null) {
            throw new InvalidLoginFormatException();
        }
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void checkPassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new InvalidPasswordException();
        }
    }
}
