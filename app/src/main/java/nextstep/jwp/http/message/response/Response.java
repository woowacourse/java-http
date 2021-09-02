package nextstep.jwp.http.message.response;

import nextstep.jwp.http.message.element.cookie.ProxyHttpCookie;

public interface Response {
    String asString();
    void setCookies(ProxyHttpCookie cookie);
}
