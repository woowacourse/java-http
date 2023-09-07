package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;

public class RootController implements Controller {
    @Override
    public String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) {
        return httpResponseBuilder.buildCustomResponse(httpRequest, "Hello world!");
    }
}
