package org.apache.coyote.http.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class StaticFileController extends FrontController {

    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8 ";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        try {
            final String url = request.getRequestLine().getUrl();
            final URL resource = getClass().getClassLoader().getResource("static" + url);
            validateNullResource(resource);
            final String responseBody = Files.readString(Paths.get(resource.toURI()));
            return HttpResponse.ok(responseBody, TEXT_CSS_CHARSET_UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }
}
