package nextstep.jwp.vo;

import java.util.Map;

import static nextstep.jwp.vo.HttpHeader.JSESSION_ID;


public class HttpCookie {
    private static final String NO_JSESSION_ID = "";

    private final Map<String, String> cookies;

    public HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getJsessionId() {
        return this.cookies.getOrDefault(JSESSION_ID.getValue(), NO_JSESSION_ID);
    }
}
