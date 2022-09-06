package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class CssResponseGenerator extends FileResponseGenerator {

    private static final String CSS_FILE_EXTENSION = ".css";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathIncluding(CSS_FILE_EXTENSION) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(generate(httpRequest.getPath()), ContentType.TEXT_CSS);
    }
}
