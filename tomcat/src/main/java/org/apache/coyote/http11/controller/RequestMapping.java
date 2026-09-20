package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpRequest;

public class RequestMapping {
    public Controller getController(HttpRequest request) {
        HttpPath httpUrl = request.getRequestLine().getHttpPath();

        if (httpUrl.startsWith("/login")) {
            return new LoginController();
        }

        if (httpUrl.startsWith("/register")) {
            return new RegisterController();
        }

        if (httpUrl.startsWith("/index.html")) {
            return new IndexController();
        }

        if (httpUrl.startsWith("/401.html")) {
            return new UnAuthorizedController();
        }

        if (httpUrl.startsWith("/css/styles.css")) {
            return new StyleController();
        }

        if (httpUrl.startsWith("/assets/chart-bar.js")) {
            return new ChartBarController();
        }

        if (httpUrl.startsWith("/js/scripts.js")) {
            return new ScriptController();
        }

        if (httpUrl.startsWith("/assets/chart-pie.js")) {
            return new ChartPieController();
        }

        if (httpUrl.startsWith("/assets/chart-area.js")) {
            return new ChartAreaController();
        }

        return new DefaultController();
    }
}
