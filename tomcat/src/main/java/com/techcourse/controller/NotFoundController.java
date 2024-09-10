package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.Resource;

public class NotFoundController extends Controller {
    private static final NotFoundController instance = new NotFoundController();

    private NotFoundController() {
    }

    public static NotFoundController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String fileName = "404.html";
        ResponseBody responseBody = new ResponseBody(Resource.read(fileName));
        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
        return response;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: GET");
    }
}
