package org.apache.catalina.mvc;

import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            return doGet(request);
        }
        if (request.isSameHttpMethod(HttpMethod.POST)) {
            return doPost(request);
        }
        log.warn("지원되지 않는 HTTP 메서드입니다.");
        throw new IllegalArgumentException("지원되지 않는 HTTP 메서드입니다.");
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        throw new UnsupportedOperationException("해당 요청을 처리하지 못했습니다.");
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        throw new UnsupportedOperationException("해당 요청을 처리하지 못했습니다.");
    }
}
