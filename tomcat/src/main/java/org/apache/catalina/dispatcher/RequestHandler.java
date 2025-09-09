package org.apache.catalina.dispatcher;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

// StaticResourceHandler(정적 리소스 처리 전용 핸들러)와 ControllerHandler(그 외의 엔드포인트)로 분리
public interface RequestHandler {

    boolean canHandle(HttpRequest request);

    void handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
