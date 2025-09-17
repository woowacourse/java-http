package com.techcourse.presentation;

public abstract class AbstractController implements Controller {

    protected abstract String getBasePath();

    @Override
    public boolean canHandle(final String uri) {
        final String basePath = getBasePath();
        return basePath != null && basePath.equals(uri);
    }

    @Override
    public final HttpResponse service(final HttpRequest request) {
        validateRequest(request);
        final String method = request.getMethod();

        if ("GET".equals(method)) {
            return doGet(request);
        }
        if ("POST".equals(method)) {
            return doPost(request);
        }

        throw new IllegalArgumentException("현재는 GET, POST 메서드만 처리 가능합니다.");
    }

    protected void validateRequest(final HttpRequest request) {
        final String uri = request.getUri();
        final String basePath = getBasePath();

        if (basePath != null && !basePath.equals(uri)) {
            throw new IllegalArgumentException("요청 경로와 일치하는 API가 존재하지 않습니다.");
        }
    }

    protected HttpResponse doGet(final HttpRequest request) {
        throw new IllegalArgumentException("GET 메서드는 지원되지 않습니다.");
    }

    protected HttpResponse doPost(final HttpRequest request) {
        throw new IllegalArgumentException("POST 메서드는 지원되지 않습니다.");
    }
}
