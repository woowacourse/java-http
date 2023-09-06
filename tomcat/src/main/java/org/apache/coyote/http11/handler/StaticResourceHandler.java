package org.apache.coyote.http11.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseUtil;

public class StaticResourceHandler implements Handler {

    private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

    @Override
    public boolean supports(HttpRequest request) {
        return doesStaticFileExists(request.getUrl());
    }

    private boolean doesStaticFileExists(String url) {
        try (final FileInputStream fileStream = new FileInputStream(
                findStaticResourceURL(url).getFile())) {
            return true;
        } catch (IOException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String url = request.getUrl();
        return ResponseUtil.buildStaticFileResponse(url);
    }

    private URL findStaticResourceURL(String url) {
        return SYSTEM_CLASS_LOADER.getResource("static" + url);
    }
}
