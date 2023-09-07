package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;

import java.io.IOException;

public class StaticController implements Controller {

    @Override
    public String process(HttpRequestParser httpRequestParser, HttpResponseBuilder httpResponseBuilder) throws IOException {
        return httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, httpRequestParser.getPath());
    }
}
