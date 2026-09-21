package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    public Controller getController(HttpRequest request) {
        if (request.hasSamePath("/login")) {
            return new LoginController();
        }

        if (request.hasSamePath("/register")) {
            return new RegisterController();
        }

        if (request.hasSamePath("/index.html")) {
            return new IndexController();
        }

        if (request.hasSamePath("/401.html")) {
            return new UnAuthorizedController();
        }

        if (request.hasSamePath("/css/styles.css")) {
            return new StyleController();
        }

        if (request.hasSamePath("/assets/chart-bar.js")) {
            return new ChartBarController();
        }

        if (request.hasSamePath("/js/scripts.js")) {
            return new ScriptController();
        }

        if (request.hasSamePath("/assets/chart-pie.js")) {
            return new ChartPieController();
        }

        if (request.hasSamePath("/assets/chart-area.js")) {
            return new ChartAreaController();
        }

        return new DefaultController();
    }
}
