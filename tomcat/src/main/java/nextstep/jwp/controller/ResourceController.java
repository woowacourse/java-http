package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import nextstep.jwp.util.FileReader;

public class ResourceController implements Controller {

    @Override
    public HttpResponse doService(HttpRequest request) {
        return HttpResponse.withoutLocation(
            request.getVersion(),
            "200 OK",
            request.getUri(),
            FileReader.read(request.getUri())
        );
    }

}
