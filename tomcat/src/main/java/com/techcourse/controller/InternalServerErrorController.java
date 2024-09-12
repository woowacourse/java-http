package com.techcourse.controller;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.util.Resource;

public class InternalServerErrorController implements Controller {
    private static final InternalServerErrorController instance = new InternalServerErrorController();
    private static final String INTERNAL_SERVER_ERROR_PATH = "500.html";

    private InternalServerErrorController() {
    }

    public static InternalServerErrorController getInstance() {
        return instance;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        ResponseBody responseBody = new ResponseBody(Resource.read(INTERNAL_SERVER_ERROR_PATH));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
    }
}
