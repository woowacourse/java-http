package nextstep.jwp.controller;

import nextstep.jwp.controller.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class IndexController extends AbstractController {

    private static final String INDEX_RESOURCE_PATH = "/index.html";

    private final HttpRequest httpRequest;

    public IndexController(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }


    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGet(httpRequest);
        }
        String body = FileReader.getFile("/404.html", getClass());
        return HttpResponse.of(ResponseStatusCode.NOT_FOUND, httpRequest.getVersion(), ContentType.HTML, body);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        String responseBody = FileReader.getFile(INDEX_RESOURCE_PATH, getClass());
        return HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), ContentType.HTML, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new IllegalArgumentException("올바르지 않은 Method 요청입니다.");
    }
}
