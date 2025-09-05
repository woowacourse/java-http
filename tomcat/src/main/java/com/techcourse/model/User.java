package com.techcourse.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public static User withId(final Long id, final String account, final String password, final String email) {
        return new User(id, account, password, email);
    }

    public boolean checkPassword(final String password) {
        return this.password.equals(password);
    }

    public boolean isPersisted() { // TODO move to superClass
        return id != null;
    }
}
