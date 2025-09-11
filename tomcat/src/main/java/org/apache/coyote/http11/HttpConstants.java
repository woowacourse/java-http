package org.apache.coyote.http11;


public final class HttpConstants {

    // ==== Common Symbols ====
    public static final String SLASH = "/";
    public static final String QUESTION = "?";
    public static final String EQUAL = "=";
    public static final String AMPERSAND = "&";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String PERIOD = ".";
    public static final String CRLF = "\r\n";

    // ==== HTTP Headers ====
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String LOCATION_HEADER = "Location";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    // ==== Pages ====
    public static final String INDEX_PAGE = "index.html";
    public static final String LOGIN_PAGE = "login.html";
    public static final String REGISTER_PAGE = "register.html";
    public static final String UNAUTHORIZED_PAGE = "401.html";
    public static final String NOT_FOUND_PAGE = "404.html";
    public static final String SERVER_ERROR_PAGE = "500.html";

    // ==== Cookies ====
    public static final String COOKIE_JSESSIONID = "JSESSIONID";

    private HttpConstants() {
    }
}
