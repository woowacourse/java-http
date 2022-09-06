package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.ContentType;

public class HtmlResponseGenerator extends FileResponseGenerator {

    private static final String HTML_FILE_EXTENSION = ".html";
    private static final String FILE_EXTENSION_PREFIX = ".";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return isBasicHtmlRequest(httpRequest) || isHtmlRequestWithoutHtmlExtension(httpRequest);
    }

    private boolean isBasicHtmlRequest(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathIncluding(HTML_FILE_EXTENSION) && httpRequest.hasHttpMethodOf(GET);
    }

    private boolean isHtmlRequestWithoutHtmlExtension(HttpRequest httpRequest) {
        return !httpRequest.hasRequestPathIncluding(FILE_EXTENSION_PREFIX) && httpRequest.hasHttpMethodOf(GET);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(generate(httpRequest.getPath()), ContentType.TEXT_HTML);
    }
}
