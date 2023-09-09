package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.method() == POST) {
            return doPost(request);
        }
        if (request.method() == GET) {
            return doGet(request);
        }
        throw new HttpException(BAD_REQUEST, "지원되지 않는 요청입니다");
    }

    protected HttpResponse doPost(HttpRequest request) {
        throw new HttpException(BAD_REQUEST, "지원되지 않는 요청입니다");
    }

    protected HttpResponse doGet(HttpRequest request) {
        throw new HttpException(BAD_REQUEST, "지원되지 않는 요청입니다");
    }
}
