package org.apache.coyote.http11.controller;

import common.http.Controller;
import common.http.ControllerManager;
import common.http.Request;
import common.http.Response;

public class StaticControllerManager implements ControllerManager {

    private static final Controller controller = new StaticResourceController();

    @Override
    public void add(String path, Controller controller) {
        throw new IllegalStateException("컨트롤러를 추가할 수 없습니다.");
    }

    @Override
    public void service(Request request, Response response) {
        controller.service(request, response);
    }
}
