package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseBuilder;

public class RootController implements Controller {
    @Override
    public String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) {
        return httpResponseBuilder.buildCustomResponse(httpRequest, "Hello world!");
    }
}
