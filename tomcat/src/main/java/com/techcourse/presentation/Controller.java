package com.techcourse.presentation;

public interface Controller {

    boolean isResponsible(final String path);

    HttpResponse getResource(final HttpRequest request);

}
