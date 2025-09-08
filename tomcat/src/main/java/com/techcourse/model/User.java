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

    @ToString.Exclude
    private final String password;

    private final String email;

    public static User withId(final Long id, final String account, final String password, final String email) {
        return new User(id, account, password, email);
    }

    public static User withoutId(final String account, final String password, final String email) {
        return new User(null, account, password, email);
    }

    public void checkPassword(final String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new IllegalArgumentException("올바르지 않은 비밀번호입니다.");
    }

    public boolean isPersisted() { // TODO move to superClass
        return id != null;
    }
}
