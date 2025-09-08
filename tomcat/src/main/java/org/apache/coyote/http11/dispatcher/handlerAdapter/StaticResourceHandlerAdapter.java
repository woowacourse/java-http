package org.apache.coyote.http11.dispatcher.handlerAdapter;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.ResourceUtil;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class StaticResourceHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        String resourcePath = httpRequest.getMappingLine().getUrl();
        URL url = ViewResolver.resolve(resourcePath);
        if (url == null) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        String resourcePath = httpRequest.getMappingLine().getUrl();
        URL url = ViewResolver.resolve(resourcePath);
        byte[] body;
        try {
            body = ResourceUtil.readAll(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contentType = URLConnection.guessContentTypeFromName(url.toString());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        int idx = contentType.indexOf("/");
        if (idx == -1) {
            contentType = "application/octet-stream";
        }

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length));
        return ResponseEntity.ok(headers, body);
    }

    private String normalize(String url) {
        if (url.startsWith("/")) {
            return url.substring(1);
        }
        return url;
    }
}
