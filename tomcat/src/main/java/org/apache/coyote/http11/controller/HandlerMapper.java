package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<Mapper, Controller> contollerByMapper = new HashMap<>();

    public HandlerMapper() {
        contollerByMapper.put(
            (request) -> "/login".equals(request.getRequestLine().getPath()) &&
                Method.POST.equals(request.getRequestLine().getMethod()),
            new LoginController(new LoginService()));

        contollerByMapper.put(
            (request) -> "/register".equals(request.getRequestLine().getPath()) &&
                Method.POST.equals(request.getRequestLine().getMethod()),
            new SignUpController(new LoginService()));

        contollerByMapper.put(
            (request) -> "/login".equals(request.getRequestLine().getPath()) &&
                Method.GET.equals(request.getRequestLine().getMethod()),
            new LoginViewController());

        contollerByMapper.put(
            (request) -> "/register".equals(request.getRequestLine().getPath()) &&
                Method.GET.equals(request.getRequestLine().getMethod()),
            new SignUpViewController());
    }

    public boolean haveAvailableHandler(Request request) {
        return contollerByMapper.keySet().stream()
            .anyMatch(mapper -> mapper.canHandle(request));
    }

    public Controller getHandler(Request request) {
        Mapper mapper = contollerByMapper.keySet().stream()
            .filter(mp -> mp.canHandle(request))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 요청을 해결할 수 있는 핸들러가 없습니다."));
        return contollerByMapper.get(mapper);
    }

    @FunctionalInterface
    private interface Mapper {

        Boolean canHandle(Request request);
    }
}
