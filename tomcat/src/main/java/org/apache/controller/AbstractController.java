package org.apache.controller;

import static nextstep.jwp.utils.FileUtils.getResource;

import org.apache.http.ContentType;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import nextstep.jwp.utils.FileUtils;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.matches(HttpMethod.GET)) {
            return handleGet(request);
        }
        if (request.matches(HttpMethod.POST)) {
           return handlePost(request);
        }
        return notFound();
    }

    protected HttpResponse handleGet(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse handlePost(HttpRequest request) {
        return notFound();
    }

    ;

    protected HttpResponse notFound() {
        return HttpResponse.of(StatusCode.NOT_FOUND, ContentType.TEXT_HTML,
            FileUtils.readFile(getResource("/404.html")));
    }
}
