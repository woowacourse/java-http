package org.apache.coyote.http11.handler;

import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.handler.controllerResponse.JsonResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.apache.coyote.http11.httpResponse.StatusLine;

public class ApiRouter {

    private final Map<String, Function<HttpRequest, ControllerResponse>> routeMap;
    private final UserController userController;

    public ApiRouter() {
        this.routeMap = new HashMap<>();
        this.userController = new UserController(new SessionManager());
        initializeRouteTable();
    }

    private void initializeRouteTable() {
        routeMap.put("GET /login", userController::loginGet);
        routeMap.put("POST /login", userController::loginPost);
        routeMap.put("GET /register", userController::registerGet);
        routeMap.put("POST /register", userController::registerPost);
    }

    public HttpResponse route(HttpRequest httpRequest) {
        try {
            Function<HttpRequest, ControllerResponse> handler = routeMap.get(httpRequest.getMethod() + " " + httpRequest.getPath());
            if (handler == null) {
                return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.NOT_FOUND), "존재하지 않는 엔드포인트입니다.");
            }
            ControllerResponse controllerResponse = handler.apply(httpRequest);
            return handleHttpResponse(controllerResponse, httpRequest.getProtocolVersion());
        } catch (Exception exception) {
            return new HttpResponse(ContentType.TEXT_HTML, new StatusLine(httpRequest.getProtocolVersion(), HttpStatus.NOT_FOUND), "서버 내부에서 오류가 발생했습니다.");
        }
    }

    private HttpResponse handleHttpResponse(ControllerResponse controllerResponse, HttpProtocolVersion protocolVersion) {
        if (controllerResponse instanceof JsonResponse) {
            HttpResponse httpResponse = new HttpResponse(ContentType.APPLICATION_JSON, new StatusLine(protocolVersion, controllerResponse.status()), controllerResponse.content());
            for (Entry<String, String> header : controllerResponse.headers().getHeaders().entrySet()) {
                httpResponse.addHeader(header.getKey(), header.getValue());
            }
            return httpResponse;
        }
        HttpResponse httpResponse = StaticFileHandler.handleDefault(protocolVersion, controllerResponse.status(), controllerResponse.content());
        for (Entry<String, String> header : controllerResponse.headers().getHeaders().entrySet()) {
            httpResponse.addHeader(header.getKey(), header.getValue());
        }
        return httpResponse;
    }
}
