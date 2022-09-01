package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.util.Optional;

public class LoginTry {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    private final String account;
    private final String password;

    public LoginTry(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String generateRedirectUrl() {
        if (this.account == null || this.password == null) {
            return FAIL_URL;
        }
        Optional<User> account = InMemoryUserRepository.findByAccount(this.account);
        if (account.isEmpty()) {
            return FAIL_URL;
        }
        User user = account.get();
        if (!user.checkPassword(this.password)) {
            return FAIL_URL;
        }
        return SUCCESS_URL;
    }
}
