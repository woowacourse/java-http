package nextstep.jwp.presentation;

import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController implements Controller {

    @Override
    public HttpResponse process(final HttpRequest httpRequest) {
        String resource = httpRequest.getRequestURL().getPath();
        return HttpResponse.ok(resource, FileReader.read(resource));
    }
}
