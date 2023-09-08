package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected static final String INDEX_URI = "/index";
    protected static final String UNAUTHORIZED_URI = "/401";
    protected static final String NOT_FOUND_URI = "/404";
    protected static final String INTERNAL_SERVER_ERROR_URI = "/500";
    protected static final String STATIC = "static";
    protected static final String HTML = ".html";

    private static final String POST = "POST";

    @Override
    public HttpResponse process(final HttpRequest request) throws IOException {
        final String requestMethod = request.getMethod();

        if (POST.equals(requestMethod)) {
            doPost(request);
        }
        return doGet(request);
    }

    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        throw new IllegalArgumentException("지원하지 않는 메서드입니다.");
    }

    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        throw new IllegalArgumentException("지원하지 않는 메서드입니다.");
    }
}
