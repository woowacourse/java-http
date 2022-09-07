package nextstep.jwp.controller;

import nextstep.jwp.controller.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public abstract class AbstractController implements Controller{

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        String body = FileReader.getFile("/404.html", getClass());
        return HttpResponse.of(ResponseStatusCode.NOT_FOUND, httpRequest.getVersion(), ContentType.HTML, body);
    }

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);
}
