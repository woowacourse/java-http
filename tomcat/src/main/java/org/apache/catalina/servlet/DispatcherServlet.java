package org.apache.catalina.servlet;

import org.apache.catalina.servlet.adapter.Controller;
import org.apache.catalina.servlet.adapter.HandlerAdapter;
import org.apache.catalina.servlet.filter.Interceptor;
import org.apache.coyote.ResponseEntity;
import org.apache.catalina.servlet.filter.SessionInterceptor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

public class DispatcherServlet implements Servlet {

    private final Map<String, Interceptor> interceptors = Map.of("/login", new SessionInterceptor());

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        if (handleInternal(request)) {
            return HttpResponse.of(
                    request.getHttpStartLine().getHttpVersion(),
                    HttpStatus.OK,
                    Map.of("Content-Type: ", HttpContentType.from(request.getHttpExtension()).getContentType()),
                    makeResponseBody("index.html"));
        }
        final Controller handler = HandlerAdapter.getHandler(request);
        final ResponseEntity responseEntity = handler.service(request);
        if (responseEntity.isRestResponse()) {
            return HttpResponse.resourceOf(
                    request.getHttpStartLine().getHttpVersion(),
                    responseEntity.getStatusCode(),
                    responseEntity.getHeaders(),
                    responseEntity.getBody());
        }
        return HttpResponse.redirectOf(
                request.getHttpStartLine().getHttpVersion(),
                responseEntity.getStatusCode(),
                responseEntity.getHeaders(),
                makeResponseBody(responseEntity.getBody()));
    }

    private boolean handleInternal(HttpRequest request) {
        return interceptors.entrySet().stream()
                .filter(interceptorEntry -> interceptorEntry.getKey().contains(request.getPath()))
                .anyMatch(interceptorEntry -> interceptorEntry.getValue().preHandle(request));
    }

    private String makeResponseBody(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(
                "static/" + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
