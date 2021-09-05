package nextstep.jwp.http;

import nextstep.jwp.http.message.HttpCookie;
import nextstep.jwp.http.message.HttpCookies;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;

public interface HttpResponse {

    String getContentAsString();
    HttpHeaders getHeaders();
    String getStatusAsString();
    String getVersionOfProtocol();
    void setContent(String content);
    void setHeaders(HttpHeaders headers);
    void setStatus(HttpStatus httpStatus);
    void setVersionOfProtocol(String versionOfProtocol);
    void addCookie(HttpCookie cookie);
    HttpCookies getCookies();
    String asString();
}
