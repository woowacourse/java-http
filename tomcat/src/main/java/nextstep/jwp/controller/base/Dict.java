package nextstep.jwp.controller.base;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.request.uri.HttpMethod;

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
        return Arrays.stream(values())
                .filter(it -> it.getPath().startsWith(path) && it.getHttpMethod().equals(httpMethod))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
