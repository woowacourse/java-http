package nextstep.jwp.web.http.response;

import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;

public interface HttpResponse {

    boolean isSuitableContentType(ContentType type);

    HttpRequest request();

    HttpHeaders headers();

    HttpProtocol protocol();

    HttpStatus status();

    String body();

    void setContentType(ContentType contentType);

    void setStatus(HttpStatus status);

    void setBody(String body);
}
