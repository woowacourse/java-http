package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;

public class RootController implements Controller {
    @Override
    public String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) {
        return httpResponseBuilder.buildCustomResponse(httpRequestParser, "Hello world!");
    }
}
