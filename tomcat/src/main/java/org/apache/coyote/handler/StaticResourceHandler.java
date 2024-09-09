package org.apache.coyote.handler;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.FileTypeChecker;
import org.apache.coyote.util.ViewResolver;

public class StaticResourceHandler implements Handler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        String targetPath = request.getTargetPath();
        if (method == HttpMethod.GET && FileTypeChecker.isSupported(targetPath)) {
            ViewResolver.resolveView(targetPath, response);
        }
    }
}
