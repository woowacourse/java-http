package org.apache.coyote.controller;

import org.apache.coyote.annotaion.GetMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ViewResolver;

public class HomeController implements Controller {

    @GetMapping("/")
    public void home(HttpRequest request, HttpResponse response) {
        ViewResolver.resolveView("index.html", response);
    }
}
