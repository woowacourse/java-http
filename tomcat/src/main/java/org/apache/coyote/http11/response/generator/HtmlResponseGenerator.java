package org.apache.coyote.http11.response.generator;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class HtmlResponseGenerator extends FileResponseGenerator {

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.isHtmlFileRequest();
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(generate(httpRequest.getFileSource()), ContentType.TEXT_HTML);
    }
}
