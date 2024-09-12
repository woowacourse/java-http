package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    private static StaticResourceController instance = new StaticResourceController();

    private StaticResourceController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new IllegalArgumentException("요청을 처리할 수 없습니다.");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
    }
}
