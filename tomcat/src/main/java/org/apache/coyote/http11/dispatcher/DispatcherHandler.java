package org.apache.coyote.http11.dispatcher;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.coyote.http11.dispatcher.handlerAdapter.HandlerAdapter;
import org.apache.coyote.http11.dispatcher.handlerAdapter.ViewResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.ResourceUtil;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class DispatcherHandler {

    private final List<HandlerAdapter> adapters;

    public DispatcherHandler(List<HandlerAdapter> adapters) {
        this.adapters = adapters;
    }

    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        for (HandlerAdapter handlerAdapter : adapters) {
            if (handlerAdapter.canHandle(httpRequest)) {
                Object object = handlerAdapter.handle(httpRequest);
                return toHttpResponse(object);
            }
        }
        return ResponseEntity.notFound();
    }

    private HttpResponse toHttpResponse(Object object) throws IOException {
        if (object == null) {
            return ResponseEntity.noContent();
        }

        if (object instanceof HttpResponse response) {
            return response;
        }

        if (object instanceof String resourcePath) {
            URL url = ViewResolver.resolve(resourcePath);
            if (url == null) {
                return ResponseEntity.notFound();
            }
            byte[] bytes = ResourceUtil.readAll(url);
            String contentType = URLConnection.guessContentTypeFromName(url.toString());
            return ResponseEntity.ok(bytes, contentType);
        }
        throw new IllegalArgumentException("Unsupported return type: " + object.getClass());
    }
}
