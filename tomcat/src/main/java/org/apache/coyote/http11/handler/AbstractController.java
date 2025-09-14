package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.handler.applicationRequest.ApplicationRequest;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.handler.applicationResponse.JsonResponse;
import org.apache.coyote.http11.httpRequest.HttpMethod;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public ApplicationResponse service(ApplicationRequest applicationRequest) {
        HttpMethod method = applicationRequest.getMethod();
        switch (method) {
            case HttpMethod.POST -> {
                return doPost(applicationRequest);
            }
            case HttpMethod.GET -> {
                return doGet(applicationRequest);
            }
            default -> {
                return new JsonResponse(HttpStatus.METHOD_NOT_ALLOWED, HttpHeaders.empty(), "지원하지 않는 요청 방식입니다.");
            }
        }
    }

    protected abstract ApplicationResponse doGet(ApplicationRequest applicationRequest);
    protected abstract ApplicationResponse doPost(ApplicationRequest applicationRequest);
}
