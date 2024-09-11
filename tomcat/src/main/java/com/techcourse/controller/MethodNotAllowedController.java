package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.Resource;

public class MethodNotAllowedController extends Controller {
    private static final MethodNotAllowedController instance = new MethodNotAllowedController();
    private static final String METHOD_NOT_ALLOWED_PATH = "405.html";

    private MethodNotAllowedController() {
    }

    public static MethodNotAllowedController getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        ResponseBody responseBody = new ResponseBody(Resource.read(METHOD_NOT_ALLOWED_PATH));
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        response.setContentType(MimeType.HTML);
        response.setBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: GET");
    }
}
