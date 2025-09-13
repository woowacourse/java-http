package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.request.ResourceResolver;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticFileController extends AbstractController {

    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8 ";

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        try {
            final String url = request.getRequestLine().getUrl();
            ResourceResolver resourceResolver = new ResourceResolver();
            URL resource = resourceResolver.resolver(url);
            final String responseBody = Files.readString(Paths.get(resource.toURI()));
            return HttpResponse.ok(responseBody, TEXT_CSS_CHARSET_UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
