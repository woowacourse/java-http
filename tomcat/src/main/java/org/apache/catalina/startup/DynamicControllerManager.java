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
        findSessionByCookie(request);

        Controller controller = mapper.get(request.getPath());
        if (controller == null) {
            return;
        }

        controller.service(request, response);

        saveNewSession(request);
    }

    private void findSessionByCookie(Request request) {
        String cookieHeader = request.getCookie();
        if (cookieHeader != null) {
            Cookie cookie = Cookie.from(cookieHeader);
            String jsessionid = Cookies.getJsessionid(cookie);
            Session session = sessionManager.findSession(jsessionid);
            request.addSession(session);
        }
    }

    private void saveNewSession(Request request) {
        Session session = request.getSession();
        if (session != null && !sessionManager.hasSession(session)) {
            sessionManager.add(session);
        }
    }
}
