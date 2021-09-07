package nextstep.jwp.response;

import nextstep.jwp.web.model.Cookie;

public interface HttpResponse {
    String toHttpResponseMessage();
    void setView(String viewName, HttpStatusCode statusCode);
    void addCookie(Cookie cookie);
}
