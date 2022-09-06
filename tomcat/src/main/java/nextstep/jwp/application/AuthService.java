package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.InvalidSignUpFormatException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.QueryParams;

public class AuthService {

    private AuthService() {
    }

    private static class AuthServiceHolder {
        private static final AuthService instance = new AuthService();
    }

    public static AuthService instance() {
        return AuthServiceHolder.instance;
    }

    public void signUp(final String requestBody) {
        QueryParams queryParams = QueryParams.parseQueryParams(requestBody);
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        String email = queryParams.get("email");
        validateSignUpFormat(account, password, email);
        validateAccountUnique(account);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateSignUpFormat(final String account, final String password, final String email) {
        if (account == null || password == null || email == null) {
            throw new InvalidSignUpFormatException();
        }
    }

    private void validateAccountUnique(final String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicateAccountException();
        }
    }

    public void login(final String requestBody) {
        QueryParams queryParams = QueryParams.parseQueryParams(requestBody);
        validateLogin(queryParams);
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
