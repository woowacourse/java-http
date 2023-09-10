package org.apache.catalina.servlet;

import org.apache.catalina.servlet.adapter.Handler;
import org.apache.catalina.servlet.adapter.HandlerAdapter;
import org.apache.catalina.servlet.filter.Interceptor;
import org.apache.catalina.servlet.filter.SessionInterceptor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet implements Servlet {

    private final Map<String, Interceptor> interceptors = Map.of("/login", new SessionInterceptor());

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (handleInternal(request)) {
            response.setHeader(request.getHttpVersion(), HttpStatus.FOUND, makeResponseHeader(request));
            response.setRedirect("index.html");
            return;
        }
        final Handler handler = HandlerAdapter.getHandler(request);
        handler.service(request, response);
    }

    private boolean handleInternal(HttpRequest request) {
        return interceptors.entrySet().stream()
                .filter(interceptorEntry -> interceptorEntry.getKey().contains(request.getPath()))
                .anyMatch(interceptorEntry -> interceptorEntry.getValue().preHandle(request));
    }

    private Map<String, String> makeResponseHeader(final HttpRequest request) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", HttpContentType.from(request.getHttpExtension()).getContentType());
        return header;
    }
}
