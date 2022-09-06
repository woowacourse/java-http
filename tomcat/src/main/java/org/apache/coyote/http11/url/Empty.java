package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Empty extends Url {
    private static final Logger log = LoggerFactory.getLogger(Empty.class);

    public Empty(final String url, final Http11Request request) {
        super(url, request);
    }

    @Override
    public Http11Response handle(HttpHeaders httpHeaders, String requestBody) {
        if (HttpMethod.GET.equals(request.getHttpMethod())) {
            log.info("path : {} ", getPath());
            return new Http11Response(getPath(), HttpStatus.OK, "Hello world!");
        }
        if (HttpMethod.POST.equals(request.getHttpMethod())) {
            throw new IllegalArgumentException("EMPTY url은 POST로 요청이 들어올수 없습니다.");
        }
        throw new IllegalArgumentException("Empty page에 해당하는 HTTP Method가 아닙니다. : " + request.getHttpMethod());
    }
}
