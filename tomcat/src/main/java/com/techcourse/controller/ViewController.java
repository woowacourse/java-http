package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import com.techcourse.exception.UnsupportedMethodException;

public class ViewController extends Controller {
    private static final ViewController instance = new ViewController();

    private final Http11Helper http11Helper = Http11Helper.getInstance();

    private ViewController() {
    }

    public static ViewController getInstance() {
        return instance;
    }

    @Override
    public String handle(HttpRequest request) throws IOException {
        String response = operate(request);
        return response;
    }

    @Override
    protected String doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected String doGet(HttpRequest request) throws IOException {
        String endpoint = request.getURI();
        String fileName = http11Helper.getFileName(endpoint);
        String response = http11Helper.createResponse(HttpStatus.OK, fileName);

        return response;
    }
}

