package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.util.Resource;

public class NotFoundController implements Controller {
    private static final NotFoundController instance = new NotFoundController();
    private static final String NOT_FOUND_PATH = "404.html";

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return instance;
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        ResponseBody responseBody = new ResponseBody(Resource.read(NOT_FOUND_PATH));
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
    }
}
