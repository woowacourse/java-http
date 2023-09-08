package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

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
