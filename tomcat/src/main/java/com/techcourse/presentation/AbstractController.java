package com.techcourse.presentation;

import com.techcourse.util.ResourceWithType;
import com.techcourse.util.StaticResourceManager;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    protected abstract String getBasePath();

    @Override
    public boolean canHandle(final String uri) {
        final String basePath = getBasePath();
        return basePath != null && basePath.equals(uri);
    }

    @Override
    public final HttpResponse service(final HttpRequest request) {
        try {
            validateRequest(request);
            final String method = request.getMethod();

            if ("GET".equals(method)) {
                return doGet(request);
            }
            if ("POST".equals(method)) {
                return doPost(request);
            }

            throw new IllegalArgumentException("현재는 GET, POST 메서드만 처리 가능합니다.");
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return createBadRequestResponse(e.getMessage(), request);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            return createInternalServerErrorResponse(request);
        }
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

    protected HttpResponse createBadRequestResponse(final String errorMessage, final HttpRequest request) {
        return HttpResponse.fromRequest(request)
                .badRequest()
                .setPlainTextContent(errorMessage)
                .build();
    }

    private HttpResponse createInternalServerErrorResponse(final HttpRequest request) {
        final ResourceWithType resource = StaticResourceManager.getResource("/500.html");

        return HttpResponse.fromRequest(request)
                .internalServerError()
                .contentType(resource.contentType())
                .setDefaultCharset()
                .contentLength(resource.content().getBytes(StandardCharsets.UTF_8).length)
                .body(resource.content())
                .build();
    }
}
