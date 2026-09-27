package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.send("text/html;charset=utf-8", "Hello world!");
    }
}
