package org.apache.coyote.http11.handler.controller.base;

import org.apache.coyote.http11.request.HttpMethod;

import java.util.Arrays;

public enum Dict {

    WELCOME_GET("/", HttpMethod.GET),
    INDEX_GET("/index.html", HttpMethod.GET),

    LOGIN_GET("/login", HttpMethod.GET),
    LOGIN_POST("/login", HttpMethod.POST),

    REGISTER_GET("/register", HttpMethod.GET),
    REGISTER_POST("/register", HttpMethod.POST),

    RESOURCE_GET("/static", HttpMethod.GET);

    private final String path;
    private final HttpMethod httpMethod;

    Dict(final String path, final HttpMethod httpMethod) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    public static Dict find(final String path, final HttpMethod httpMethod) {
        System.out.println(path + " " + httpMethod.name());
        return Arrays.stream(values())
                .filter(it -> it.getPath().startsWith(path) && it.getHttpMethod().equals(httpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 요청을 처리할 수 없습니다."));
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
