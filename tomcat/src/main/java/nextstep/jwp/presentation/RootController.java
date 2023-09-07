package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;

public class RootController implements Controller {
    @Override
    public String process(HttpRequest httpRequest, HttpResponse httpResponse) {
        return HttpResponseBuilder.buildCustomResponse(httpRequest, httpResponse, "Hello world!");
    }
}
