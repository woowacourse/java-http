package com.techcourse.controller;

import java.util.Set;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            ".html", ".css", ".js", ".ico", ".svg"
    );

    @Override
    public boolean isProvidableUrl(final String path) {
        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return false;
        }
        String extension = path.substring(lastDotIndex);
        return SUPPORTED_EXTENSIONS.contains(extension);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        final String path = request.getPath();

        // 경로 조작 공격 방지
        if (path.contains("..")) {
            log.warn("directory scan attack detected! : {}", path);
            response.setNotFound();
            return;
        }

        final String resourcePath = path.substring(1);
        response.setOk(resourcePath);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        response.setInternalServerError();
    }
}
