package com.techcourse.presentation;

import com.techcourse.util.ResourceWithType;
import com.techcourse.util.StaticResourceManager;
import java.nio.charset.StandardCharsets;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canHandle(final String uri) {
        return false;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        final String uri = request.getUri();

        if (!StaticResourceManager.isStaticResource(uri)) {
            return HttpResponse.fromRequest(request)
                    .notFound()
                    .location("/404.html")
                    .build();
        }

        final ResourceWithType resource = StaticResourceManager.getResource(uri);
        final String statusCode = getStatusCode(uri);

        return HttpResponse.fromRequest(request)
                .statusCode(statusCode)
                .contentType(resource.contentType())
                .setDefaultCharset()
                .contentLength(resource.content().getBytes(StandardCharsets.UTF_8).length)
                .body(resource.content())
                .build();
    }

    @Override
    protected String getBasePath() {
        return null;
    }

    private String getStatusCode(final String pathName) {
        return switch (pathName) {
            case "/401.html" -> "401 Unauthorized";
            case "/404.html" -> "404 Not Found";
            case "/500.html" -> "500 Internal Server Error";
            default -> "200 OK";
        };
    }
}
