package support;

import nextstep.jwp.model.User;

public enum UserFixture {

    USER_A("account_a", "password", "email@a.com");

    public final String account;
    public final String password;
    public final String email;

    UserFixture(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User buildUser() {
        return new User(account, password, email);
    }
}
