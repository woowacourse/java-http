package com.techcourse.controller;

import com.techcourse.exception.client.BadRequestException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw new BadRequestException("요청한 HTTP 메서드는 이 리소스에서 허용되지 않습니다");
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new BadRequestException("요청한 HTTP 메서드는 이 리소스에서 허용되지 않습니다");
    }
}
