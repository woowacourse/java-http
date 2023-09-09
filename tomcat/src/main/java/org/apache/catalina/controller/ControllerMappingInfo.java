package org.apache.catalina.controller;

import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.HttpRequest;

import java.util.Objects;

public class ControllerMappingInfo {

    private final HttpMethod httpMethod;
    private final boolean isParameterized;
    private final String requestPath;

    private ControllerMappingInfo(final HttpMethod httpMethod, final boolean isParameterized, final String requestPath) {
        this.httpMethod = httpMethod;
        this.isParameterized = isParameterized;
        this.requestPath = requestPath;
    }

    public static ControllerMappingInfo of(final HttpMethod httpMethod, final boolean isParameterized, final String requestPath) {
        return new ControllerMappingInfo(httpMethod, isParameterized, requestPath);
    }

    public static ControllerMappingInfo from(final HttpRequest request) {
        return new ControllerMappingInfo(
                request.httpMethod(),
                !request.queryParams().isEmpty(),
                request.requestPath().value()
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ControllerMappingInfo that = (ControllerMappingInfo) o;
        return isParameterized == that.isParameterized && httpMethod == that.httpMethod && Objects.equals(requestPath, that.requestPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, isParameterized, requestPath);
    }

    @Override
    public String toString() {
        return "ControllerMappingInfo{" +
               "httpMethod=" + httpMethod +
               ", isParameterized=" + isParameterized +
               ", requestPath='" + requestPath + '\'' +
               '}';
    }
}
