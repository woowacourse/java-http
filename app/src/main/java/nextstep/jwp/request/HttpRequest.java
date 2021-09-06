package nextstep.jwp.request;

import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.web.model.HttpCookie;
import nextstep.jwp.web.model.HttpSession;

public interface HttpRequest {

    RequestLine getRequestLine();
    RequestHeader getRequestHeader();
    String getResourceName();
    String getAttribute(String name);
    HttpCookie getCookies();
    HttpSession getSession(HttpResponse response);
}
