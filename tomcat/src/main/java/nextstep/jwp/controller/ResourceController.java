package nextstep.jwp.controller;

import nextstep.jwp.controller.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class ResourceController extends AbstractController {

    private final HttpRequest httpRequest;

    public ResourceController(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse service() {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGet();
        }
        return HttpResponse.createMethodNotAllowed(httpRequest, getClass());
    }

    @Override
    protected HttpResponse doGet() {
        String path = httpRequest.getRequestTarget();
        ContentType contentType = ContentType.from(path);
        String responseBody = FileReader.getFile(path, getClass());
        return HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), contentType
                ,responseBody);
    }

    @Override
    protected HttpResponse doPost() {
        throw new IllegalArgumentException("올바르지 않은 Method 요청입니다.");
    }
}
