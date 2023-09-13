package org.apache.coyote.http11.controller;

import common.http.Controller;
import common.http.ControllerManager;
import common.http.Request;
import common.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticControllerManager implements ControllerManager {

    private static final StaticControllerManager instance = new StaticControllerManager();
    private static final Controller controller = new StaticResourceController();
    private static final Logger log = LoggerFactory.getLogger(StaticControllerManager.class);

    private StaticControllerManager() {}

    public static StaticControllerManager getInstance() {
        return instance;
    }

    @Override
    public void add(String path, Controller controller) {
        throw new IllegalStateException("컨트롤러를 추가할 수 없습니다.");
    }

    @Override
    public void service(Request request, Response response) {
        log.info("Controller: {}", this.getClass().getName());
        controller.service(request, response);
    }
}
