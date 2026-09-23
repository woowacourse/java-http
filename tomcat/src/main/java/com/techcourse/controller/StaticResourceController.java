package com.techcourse.controller;

import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseContentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class StaticResourceController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);

    private final ResponseContentResolver responseContentResolver;

    public StaticResourceController() {
        this(new ResponseContentResolver());
    }

    public StaticResourceController(final ResponseContentResolver responseContentResolver) {
        this.responseContentResolver = Objects.requireNonNull(responseContentResolver);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        try {
            return HttpResponse.ok(responseContentResolver.resolve(request.path()));
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.error(e.status());
        }
    }
}
