package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.util.FileReader;

public class ResourceController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {
        String uri = request.getUri();
        return HttpResponse.ok(uri, FileReader.read(uri));
    }
}
