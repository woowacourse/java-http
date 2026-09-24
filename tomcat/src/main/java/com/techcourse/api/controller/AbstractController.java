package com.techcourse.api.controller;

import com.techcourse.api.Controller;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        throw new UnsupportedOperationException("지원하지 않는 HTTP 메서드입니다.");
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;

    // TODO: 지금 login만 (파라미터가 2개인 경우만) 신경쓰는 중
    protected final Map<String, String> parseRequestBody(final HttpRequest request) {
        final Map<String, String> parameters = new HashMap<>();
        if (request.getBody().isBlank()) {
            return parameters;
        }

        for (String pair : request.getBody().split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(nameAndValue[0].trim(), nameAndValue[1].trim());
            }
        }
        return parameters;
    }
}
