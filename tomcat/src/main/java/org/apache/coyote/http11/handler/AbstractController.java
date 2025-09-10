package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.handler.controllerResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpMethod;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public abstract class AbstractController implements Controller {

    @Override
    public ApplicationResponse service(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        switch (method) {
            case HttpMethod.POST -> {
                return doPost(httpRequest);
            }
            case HttpMethod.GET -> {
                return doGet(httpRequest);
            }
        }
        throw new IllegalArgumentException("지원하지 않는 요청 방식입니다.");
    }

    protected abstract ApplicationResponse doGet(HttpRequest httpRequest);
    protected abstract ApplicationResponse doPost(HttpRequest httpRequest);
}
