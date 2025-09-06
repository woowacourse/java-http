package org.apache.coyote.http11.handler;

import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;

public class ApiRouter {

    private final Map<String, Function<HttpRequest, HttpResponse>> routeMap;
    private final UserController userController;

    public ApiRouter() {
        this.routeMap = new HashMap<>();
        this.userController = new UserController();
        initializeRouteTable();
    }

    private void initializeRouteTable() {
        routeMap.put("/login", userController::login);
    }

    public HttpResponse route(HttpRequest httpRequest) {
        Function<HttpRequest, HttpResponse> handler = routeMap.get(httpRequest.getPath());
        if (handler == null) {
            return new HttpResponse("404 Not Found", "text/html;charset=utf-8", null);
        }
        return handler.apply(httpRequest);
    }
}
