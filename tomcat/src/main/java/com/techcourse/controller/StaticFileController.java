package com.techcourse.controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticFileController extends AbstractController {

    private static final List<String> SUPPORT_EXTENSIONS = List.of(
            ".html", ".css", ".js"
    );

    @Override
    public boolean support(HttpRequest request) {
        String requestUrl = request.getRequestUrl();

        for (String supportExtension : SUPPORT_EXTENSIONS) {
            if (requestUrl.endsWith(supportExtension)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource("static" + request.getRequestUrl());

        if (resource == null) {
            response.redirect("404.html");
            return;
        }

        Path resourcePath = Path.of(resource.getPath());

        response.ok()
                .writeStaticResource(resourcePath);
    }
}
