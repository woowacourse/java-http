package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import com.techcourse.exception.UnsupportedMethodException;
import com.techcourse.util.Resource;

public class StaticResourceController extends AbstractController {
    private static final StaticResourceController instance = new StaticResourceController();

    private StaticResourceController() {
    }

    public static StaticResourceController getInstance() {
        return instance;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedMethodException("Method is not supported: POST");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String fileName = Resource.getFileName(request.getURI());
        ResponseBody responseBody = new ResponseBody(Resource.read(fileName));
        response.setStatus(HttpStatus.OK);
        response.setContentType(MimeType.getMimeType(fileName));
        response.setBody(responseBody);
    }
}
