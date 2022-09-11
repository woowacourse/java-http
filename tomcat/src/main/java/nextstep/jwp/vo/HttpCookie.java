package nextstep.jwp.vo;

import java.util.Map;

import static nextstep.jwp.vo.HeaderKey.JSESSION_ID;


public class HttpCookie {
    private static final String NO_COOKIE = "";
    private static final String COOKIE_DELIMITER = "; ";

    private final FormData cookies;

    private HttpCookie(FormData cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(Map<String, String> headers) {
        FormData formData = FormData.from(headers.getOrDefault(HeaderKey.COOKIE.getName(), NO_COOKIE).split(COOKIE_DELIMITER));
        return new HttpCookie(formData);
    }

    public String getJsessionId() {
        return this.cookies.get(JSESSION_ID.getName());
    }
}
