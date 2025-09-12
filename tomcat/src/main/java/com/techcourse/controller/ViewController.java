package com.techcourse.controller;

import com.techcourse.HttpStaus;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResponseHandler;

public class ViewController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        ResponseHandler.sendDefaultResource(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        //405를 보내고 싶었지만, 조재하는 html에 없어 500으로 대체하였습니다.
        ResponseHandler.sendStaticFile(response,"/500.html", HttpStaus.INTERNAL_SERVER_ERROR);
    }
}
