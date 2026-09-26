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
        var resource = staticResource.read(request.getPath());
        if (resource.isPresent()) {
            response.setContentType(resolveContentType(request.getPath()));
            response.setBody(resource.get());
            return;
        }

        response.setStatus(HttpStatus.NOT_FOUND);
        response.setContentType("text/html;charset=utf-8");
        response.setBody(staticResource.read("/404.html").orElse(new byte[0]));
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
