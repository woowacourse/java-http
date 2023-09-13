package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<Mapper, Controller> controllerByMapper = new HashMap<>();

    public HandlerMapper() {
        enrollHandler();
    }

    private void enrollHandler() {
        controllerByMapper.put(
            request -> "/login".equals(request.getRequestLine().getPath()),
            new LoginController(new LoginService()));

        controllerByMapper.put(
            request -> "/register".equals(request.getRequestLine().getPath()),
            new SignUpController(new LoginService()));
    }

    public boolean haveAvailableHandler(HttpRequest httpRequest) {
        return controllerByMapper.keySet().stream()
            .anyMatch(mapper -> mapper.canHandle(httpRequest));
    }

    private Controller getHandler(HttpRequest httpRequest) {
        Mapper mapper = controllerByMapper.keySet().stream()
            .filter(mp -> mp.canHandle(httpRequest))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 요청을 해결할 수 있는 핸들러가 없습니다."));
        return controllerByMapper.get(mapper);
    }

    public String controllerResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        Controller handler = getHandler(httpRequest);
        handler.service(httpRequest, httpResponse);
        return httpResponse.toString();
    }

    @FunctionalInterface
    private interface Mapper {

        Boolean canHandle(HttpRequest httpRequest);
    }
}
