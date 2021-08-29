package nextstep.jwp.http;

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
    String asString();
}
