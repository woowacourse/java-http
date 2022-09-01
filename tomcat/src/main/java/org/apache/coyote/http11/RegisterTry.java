package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterTry {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    private final String account;
    private final String password;

    private final String email;

    public RegisterTry(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public String signUp() {
        try {
            User user = new User(this.account, this.password, this.email);
            InMemoryUserRepository.save(user);
            return SUCCESS_URL;
        }
        catch (IllegalArgumentException e) {
            return FAIL_URL;
        }
    }
}
