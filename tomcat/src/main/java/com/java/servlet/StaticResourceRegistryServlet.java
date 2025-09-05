package com.java.servlet;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.java.http.HttpRequest.HttpMethod.GET;

public class StaticResourceRegistryServlet implements Servlet {

    private final Map<String, String> uri_resourcePath = new HashMap<>();

    public StaticResourceRegistryServlet register(String uri, String resourcePath) {
        uri_resourcePath.put(uri, resourcePath);
        return this;
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && uri_resourcePath.get(request.uri()) != null;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String resourcePath = uri_resourcePath.get(request.uri());
        if (resourcePath.endsWith(".html")) return HttpResponse.ok().html(file(resourcePath)).build();
        if (resourcePath.endsWith(".css")) return HttpResponse.ok().css(file(resourcePath)).build();
        if (resourcePath.endsWith(".js")) return HttpResponse.ok().js(file(resourcePath)).build();
        if (resourcePath.endsWith(".ico")) return HttpResponse.ok().icon(file(resourcePath)).build();
        throw new UnsupportedOperationException("지원되지 않는 확장자입니다. path=" + resourcePath);
    }

    private byte[] file(String resourcePath) {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) throw new IllegalStateException("존재하지 않는 정적 리소스입니다. path=" + resourcePath);
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("정적 리소스를 읽는 중 예외가 발생했습니다.", e);
        }
    }
}
