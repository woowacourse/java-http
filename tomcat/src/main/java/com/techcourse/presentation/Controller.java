package com.techcourse.presentation;

public interface Controller {

    boolean canHandle(String uri);

    HttpResponse service(HttpRequest request);

}
