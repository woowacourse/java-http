package nextstep.jwp.request;

import nextstep.jwp.web.model.HttpCookie;

public interface HttpRequest {

    RequestLine getRequestLine();
    RequestHeader getRequestHeader();
    String getResourceName();
    String getAttribute(String name);
    HttpCookie getCookies();
}
