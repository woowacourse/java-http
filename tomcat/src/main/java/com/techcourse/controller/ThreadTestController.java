package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.exception.CoyoteException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ThreadTestController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new CoyoteException(e);
        }

        response.setStaticResource("index.html");
    }
}
