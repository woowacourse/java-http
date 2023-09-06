package org.apache.coyote.http11.domain;

public class HttpRequestBody {

    private final String account;
    private final String email;
    private final String password;

    private HttpRequestBody(final String account, final String email, final String password) {
        this.account = account;
        this.email = email;
        this.password = password;
    }

    public static HttpRequestBody from(final String body) {
        final String[] splitBodies = body.split("&");
        final String account = removePrefix(splitBodies[0]);
        final String email = removePrefix(splitBodies[1]);
        final String password = removePrefix(splitBodies[2]);
        return new HttpRequestBody(account, email, password);
    }

    private static String removePrefix(final String splitBody) {
        final String[] strings = splitBody.split("=");
        return strings[1];
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
