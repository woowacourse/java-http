package org.apache.catalina.controller;

import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(ServletRequest request, ServletResponse response) {
        switch (request.getMethod()) {
            case HttpMethod.GET -> doGet(request, response);
            case HttpMethod.POST -> doPost(request, response);
            default -> throw new UnsupportedOperationException("지원하지 않는 요청 방식입니다: " + request.getMethod());
        }
    }

    protected abstract void doGet(ServletRequest request, ServletResponse response);

    protected abstract void doPost(ServletRequest request, ServletResponse response);
}
