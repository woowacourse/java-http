package com.techcourse.controller.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.response.HttpResponse;

public record HttpResponseEntity<T>(HttpStatus httpStatus, T body, Map<String, String> headers) {

    public HttpResponseEntity() {
        this(HttpStatus.OK, null, new LinkedHashMap<>());
    }

    public HttpResponseEntity(HttpStatus httpStatus, T body) {
        this(httpStatus, body, new LinkedHashMap<>());
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(HttpCookie httpCookie) {
        headers.put(HttpHeaders.SET_COOKIE, httpCookie.getCookieToMessage());
    }

    public HttpResponse<?> convertResponse() {
        return new HttpResponse<>(httpStatus, headers, body);
    }
}
