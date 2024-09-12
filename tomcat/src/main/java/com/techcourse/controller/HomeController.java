package com.techcourse.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import com.techcourse.servlet.view.StaticResourceView;

public class HomeController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        new StaticResourceView("index.html").render(response);
    }
}