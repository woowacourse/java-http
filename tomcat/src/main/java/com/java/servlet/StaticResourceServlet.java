package com.java.servlet;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

import java.io.IOException;

import static com.java.http.HttpRequest.HttpMethod.GET;

public abstract class StaticResourceServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals(resourcePath());
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (resourcePath().endsWith(".html")) return HttpResponse.ok().html(file()).build();
        if (resourcePath().endsWith(".css")) return HttpResponse.ok().css(file()).build();
        if (resourcePath().endsWith(".js")) return HttpResponse.ok().js(file()).build();
        if (resourcePath().endsWith(".ico")) return HttpResponse.ok().icon(file()).build();
        throw new UnsupportedOperationException("지원되지 않는 확장자입니다. path=" + resourcePath());
    }

    private byte[] file() {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath())) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("정적 리소스를 읽는 중 예외가 발생했습니다.", e);
        }
    }

    protected abstract String resourcePath();
}
