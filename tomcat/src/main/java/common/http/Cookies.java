package common.http;

public class Cookies {

    private static final String JSESSIONID = "JSESSIONID";

    private Cookies() {}

    public static Cookie ofJSessionId(String id) {
        return Cookie.from(JSESSIONID + Cookie.DELIMITER + id);
    }

    public static String getJsessionid(Cookie cookie) {
        if (cookie.hasAttribute(JSESSIONID)) {
            return cookie.getAttribute(JSESSIONID);
        }
        throw new IllegalArgumentException("쿠키에 세션 아이디가 없습니다.");
    }
}
