package org.apache.catalina.controller;

import org.apache.catalina.util.StaticResources;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Optional;

public class StaticResourceController implements Controller {

    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String PAGE_NOT_FOUND_MESSAGE = "서버 오류가 발생했습니다.";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final String path = request.getPath();
        final Optional<String> body = StaticResources.read(path);

        if (body.isEmpty()) {
            StaticResources.read(NOT_FOUND_PAGE).ifPresentOrElse(
                    resource -> response.sendError(HttpStatus.BAD_REQUEST, resource),
                    () -> response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, PAGE_NOT_FOUND_MESSAGE));
            return;
        }
        response.ok(ContentType.from(path), body.get());
    }
}
