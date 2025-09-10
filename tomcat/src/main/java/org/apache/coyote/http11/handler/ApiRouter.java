package org.apache.coyote.http11.handler;

import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.handler.controllerResponse.ApplicationResponse;
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
        Controller controller = routingTable.findControllerOfPath(httpRequest.getPath());
        if (controller == null) {
            return HttpResponse.of(httpRequest.getProtocolVersion(), HttpStatus.NOT_FOUND, ContentType.TEXT_HTML, "존재하지 않는 엔드포인트입니다.");
        }
        ApplicationResponse applicationResponse = controller.service(httpRequest);
        return handleHttpResponse(applicationResponse, httpRequest.getProtocolVersion());
    }

    private HttpResponse handleHttpResponse(ApplicationResponse applicationResponse, HttpProtocolVersion protocolVersion) {
        if (applicationResponse instanceof JsonResponse) {
            HttpResponse httpResponse = HttpResponse.of(protocolVersion, applicationResponse.status(), ContentType.APPLICATION_JSON, applicationResponse.content());
            addHeadersFromControllerResponse(httpResponse, applicationResponse);
            return httpResponse;
        }
        HttpResponse httpResponse = StaticFileHandler.handleDefault(protocolVersion, applicationResponse.content());
        addHeadersFromControllerResponse(httpResponse, applicationResponse);
        return httpResponse;
    }

    private void addHeadersFromControllerResponse(HttpResponse httpResponse, ApplicationResponse applicationResponse) {
        for (Entry<String, String> header : applicationResponse.headers().getHeaders().entrySet()) {
            httpResponse.addHeader(header.getKey(), header.getValue());
        }
    }
}
