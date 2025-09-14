package com.techcourse.web.request;

import common.HttpMethod;
import common.session.Session;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppRequest {

    @Getter
    private final HttpMethod method;

    @Getter
    private final String path;

    @Getter
    private final Session session;

    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Map<String, String> bodyParams;

    public static AppRequest of(
            final HttpMethod method,
            final String path,
            final Map<String, String> queryParams,
            final Map<String, String> bodyParams,
            final Map<String, String> headers,
            final Session session
    ) {
        return new AppRequest(method, path, session, headers, queryParams, bodyParams);
    }

    public String getHeader(final String name) {
        if (name == null) {
            return null;
        }
        return headers.get(name.toLowerCase());
    }

    public String getQueryParam(final String name) {
        return queryParams.get(name);
    }

    public String getBodyParam(final String name) {
        return bodyParams.get(name);
    }
}
