package nextstep.jwp.http.message.response;

import nextstep.jwp.http.message.element.cookie.ProxyCookie;

public interface Response {
    String asString();
    void setCookies(ProxyCookie cookie);
}
