package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import http.HttpMethod;
import http.HttpStatusCode;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class RootPresentation implements Presentation {

    private static final String URI_PATH = "/";

    @Override
    public HttpResponse view(HttpRequest request) {
        String responseBody = "Hello world!";
        return new HttpResponse(HttpStatusCode.OK, request.getMediaType(), responseBody);
    }

    @Override
    public boolean match(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.GET &&
                URI_PATH.equals(request.getPath()) &&
                request.getQueryParam().isEmpty();
    }
}
