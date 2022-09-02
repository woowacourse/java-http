package org.apache.coyote.http11.dto;

public class Http11Request {

    private final String account;
    private final String password;
    private final String path;

    public Http11Request(String path) {
        this(null, null, path);
    }

    public Http11Request(String account, String password, String path) {
        this.account = account;
        this.password = password;
        this.path = path;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getPath() {
        return path;
    }
}
