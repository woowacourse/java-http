package nextstep.jwp.db;

public class Cookies {

    public static HttpCookie ofJSessionId(String id) {
        return new HttpCookie("JSESSIONID=" +id);
    }
}
