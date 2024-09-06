package com.techcourse.controller;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;

import java.io.IOException;

public class Controller {
    private final ResponseCreator responseCreator;

    public Controller() {
        this.responseCreator = new ResponseCreator();
    }

    public String getHelloWorldPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getPath());
    }

    public String getDefaultPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getPath());
    }

}
