package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RootController extends AbstractController {

    private static final String ROOT_MESSAGE = "Hello world!";

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        throw new IllegalArgumentException("지원하지 않는 핸들러입니다.");
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        Response createdResponse = Response.createByResponseBody(HttpStatus.OK,
                new ResponseBody(ROOT_MESSAGE, ContentType.HTML));
        response.setBy(createdResponse);
    }
}
