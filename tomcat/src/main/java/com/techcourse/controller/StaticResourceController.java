package com.techcourse.controller;

import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResource;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController extends AbstractController {

    private final StaticResource staticResource;

    public StaticResourceController(StaticResource staticResource) {
        this.staticResource = staticResource;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        byte[] body = staticResource.read(request.getPath())
                .orElseGet(() -> readNotFoundPage(response));

        response.setContentType(resolveContentType(request.getPath()));
        response.setBody(body);
    }

    private byte[] readNotFoundPage(HttpResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND);
        try {
            return staticResource.read("/404.html").orElse(new byte[0]);
        } catch (Exception ignored) {
            return new byte[0];
        }
    }

    private String resolveContentType(String path) {
        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex < 0) {
            return "text/html;charset=utf-8";
        }

        return switch (path.substring(extensionIndex + 1).toLowerCase()) {
            case "css" -> "text/css;charset=utf-8";
            case "js" -> "application/javascript;charset=utf-8";
            case "svg" -> "image/svg+xml";
            default -> "text/html;charset=utf-8";
        };
    }
}
