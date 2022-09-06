package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class JSResponseGenerator extends FileResponseGenerator {

    private static final String JS_FILE_EXTENSION = ".js";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathIncluding(JS_FILE_EXTENSION) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(generate(httpRequest.getPath()), ContentType.TEXT_JS);
    }
}
