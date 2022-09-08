package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    protected abstract HttpResponse doPost(HttpRequest httpRequest);

    protected abstract HttpResponse doGet(HttpRequest httpRequest);

    protected HttpResponse doNotFoundRequest(HttpRequest request) {
        return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                .addStatus(HttpStatus.NOT_FOUND)
                .addResponseBody("페이지를 찾을 수 없습니다.", ContentType.TEXT_HTML_CHARSET_UTF_8);
    }
}
