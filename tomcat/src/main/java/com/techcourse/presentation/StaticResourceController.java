package com.techcourse.presentation;

import org.apache.coyote.http11.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);

    @Override
    protected void validateRequest(HttpRequest request) {
        final String uri = request.requestLine().getUri();
        if (!isStaticResource(uri)) {
            log.debug("요청 경로: {}", uri);
            throw new IllegalArgumentException("요청 경로에 해당하는 정적 자원이 없습니다.");
        }
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        final RequestLine requestLine = request.requestLine();
        return renderStaticPage(requestLine.getUri(), requestLine.getProtocol());
    }

    @Override
    protected String getBasePath() {
        return "";
    }
}
