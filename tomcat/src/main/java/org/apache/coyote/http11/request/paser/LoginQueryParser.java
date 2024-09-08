package org.apache.coyote.http11.request.paser;

public class LoginQueryParser extends QueryParser {

    public LoginQueryParser(String query) {
        super(query);
    }

    public String findAccount() {
        return findValue("account");
    }

    public String findPassword() {
        return findValue("password");
    }
}
