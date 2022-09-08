package org.apache.controller;

import static nextstep.jwp.utils.FileUtils.getResource;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.StatusCode;
import nextstep.jwp.utils.FileUtils;

public abstract class AbstractController implements Controller{

    @Override
    public void service(HttpRequest request) throws Exception {
        if (request.matches(HttpMethod.GET)) {
            handleGet(request);
        }
        if (request.matches(HttpMethod.POST)) {
            handlePost(request);
        }
    }

    protected HttpResponse handleGet(HttpRequest request) throws Exception {
        return notfound();
    }
    protected HttpResponse handlePost(HttpRequest request) throws Exception {
        return notfound();
    };

    protected HttpResponse notfound() {
        return HttpResponse.of(StatusCode.NOT_FOUND, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/404.html")));
    }
}
