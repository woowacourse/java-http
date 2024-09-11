package com.techcourse.controller;

import java.util.Objects;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

public class Request {
    private final HttpMethod method;
    private final String path;

    private Request(final HttpMethod method, final String path) {
        this.method = method;
        this.path = path;
    }

    public static Request ofGet(final String path) {
        return new Request(HttpMethod.GET, path);
    }

    public static Request ofPost(final String path) {
        return new Request(HttpMethod.POST, path);
    }

    public static Request of(final HttpRequest request) {
        return new Request(request.getRequestMethod(), request.getRequestPath());
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request request = (Request) o;
        return getMethod() == request.getMethod() && Objects.equals(getPath(), request.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getPath());
    }
}
