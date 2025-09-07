package org.apache.coyote.http11.handler;

import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.handler.controllerResponse.JsonResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class ApiRouter {

    private final Map<String, Function<HttpRequest, ControllerResponse>> routeMap;
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
        try {
            Function<HttpRequest, ControllerResponse> handler = routeMap.get(httpRequest.getPath());
            if (handler == null) {
                return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "text/html;charset=utf-8", "서버 내부에서 오류가 발생했습니다.");
            }

            ControllerResponse controllerResponse = handler.apply(httpRequest);
            return handleHttpResponse(controllerResponse);
        } catch (Exception exception) {
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "application/json;charset=utf-8", "서버 내부에서 오류가 발생했습니다.");
        }
    }

    private HttpResponse handleHttpResponse(ControllerResponse controllerResponse) {
        if (controllerResponse instanceof JsonResponse) {
            return new HttpResponse(controllerResponse.status(), "application/json;charset=utf-8", controllerResponse.content());
        }
        return StaticFileHandler.handleDefault(controllerResponse.content());
    }
}
