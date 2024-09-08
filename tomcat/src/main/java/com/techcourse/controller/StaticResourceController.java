package com.techcourse.controller;

import com.techcourse.controller.model.AbstractController;
import java.net.URL;
import org.apache.coyote.http11.domain.ResourceFinder;
import org.apache.coyote.http11.domain.body.ContentType;
import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.domain.RequestPath;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.response.domain.HttpStatus;
import org.apache.coyote.http11.response.maker.HttpResponseMaker;
import org.apache.coyote.http11.response.model.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestPath requestPath = httpRequest.getRequestPath();

        URL resourceUrl = StaticResourceController.class.getClassLoader()
                .getResource(requestPath.toResourcePath());
        return resourceUrl != null;
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException("해당되는 메서드의 요청을 찾지 못했습니다.");
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        String resource = ResourceFinder.find(httpRequest.getRequestPath());
        ContentType contentType = findResourceExtension(httpRequest.getRequestLine());

        return HttpResponseMaker.make(resource, contentType, HttpStatus.OK);
    }

    private ContentType findResourceExtension(RequestLine requestLine) {
        String extension = requestLine.getRequestPathExtension();
        return ContentType.findContentType(extension);
    }
}
