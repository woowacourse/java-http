package org.apache.catalina.startup;

import common.http.Controller;
import common.http.ControllerManager;
import common.http.Cookie;
import common.http.Cookies;
import common.http.Request;
import common.http.Response;
import common.http.Session;

import java.util.HashMap;
import java.util.Map;

public class DynamicControllerManager implements ControllerManager {

    private static final Map<String, Controller> mapper = new HashMap<>();

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void add(String path, Controller controller) {
        mapper.put(path, controller);
    }

    @Override
    public void service(Request request, Response response) {
        String cookieHeader = request.getCookie();
        if (cookieHeader != null) {
            Session session = sessionManager.findSession(Cookies.getJsessionid(Cookie.from(cookieHeader)));
            request.addSession(session);
        }

        Controller controller = mapper.get(request.getPath());
        if (controller == null) {
            return;
        }

        controller.service(request, response);

        Session session = request.getSession();
        if (session != null && !sessionManager.hasSession(session)) {
            sessionManager.add(session);
        }
    }
}
