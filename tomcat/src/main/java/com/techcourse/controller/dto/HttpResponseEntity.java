package com.techcourse.controller.dto;

import java.util.EnumMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public record HttpResponseEntity<T>(HttpStatus httpStatus, T body, Map<HttpHeaders, String> headers) {

    public HttpResponseEntity() {
        this(HttpStatus.OK, null, new EnumMap<>(HttpHeaders.class));
    }

    public HttpResponseEntity(T body) {
        this(HttpStatus.OK, body, new EnumMap<>(HttpHeaders.class));
    }

    public HttpResponseEntity(HttpStatus httpStatus, T body) {
        this(httpStatus, body, new EnumMap<>(HttpHeaders.class));
    }

    public void addHeader(HttpHeaders key, String value) {
        headers.put(key, value);
    }

    public HttpResponse<?> convertResponse() {
        return new HttpResponse<>(httpStatus, body, headers);
    }
}
