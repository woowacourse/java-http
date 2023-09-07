package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponseBuilder;

import java.io.IOException;

public class NotFoundController implements Controller {

    @Override
    public String process(HttpRequest httpRequest, HttpResponseBuilder httpResponseBuilder) throws IOException {
        return httpResponseBuilder.buildStaticFileNotFoundResponse(httpRequest);
    }
}
