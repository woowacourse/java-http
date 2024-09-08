package com.techcourse.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;

import java.io.IOException;

public class StaticResourceController implements Controller {

    private final ResponseCreator responseCreator;

    public StaticResourceController() {
        this.responseCreator = new ResponseCreator();
    }

    @Override
    public String execute(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getUrl());
    }
}
