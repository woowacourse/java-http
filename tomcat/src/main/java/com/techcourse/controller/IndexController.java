package com.techcourse.controller;

import org.apache.catalina.DispatchResult;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.WebController;
import org.apache.coyote.error.HttpException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

@WebController(path = "/index")
public class IndexController extends AbstractController {

    @Override
    protected DispatchResult doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw HttpException.methodNotAllowed();
    }

    @Override
    protected DispatchResult doGet(HttpRequest request, HttpResponse response) throws Exception {
        return DispatchResult.forward(HttpStatus.OK, "/index.html");
    }
}
