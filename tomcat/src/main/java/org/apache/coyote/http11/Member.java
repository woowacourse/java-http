package org.apache.coyote.http11;

public class Member {

    private final String account;
    private final String password;
    private final String email;

    public Member(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
