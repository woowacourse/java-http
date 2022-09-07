package org.apache.coyote.http11.handler;

import java.util.UUID;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class IndexHandler extends ResourceHandler {

    private static final String INDEX_HTML = "/index.html";
    private static final String EQUAL_LETTER = "=";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final HttpHeaders headers = httpRequest.getHeaders();
        if (!headers.contains("Cookie")) {
            final HttpHeader httpHeader = HttpHeader.of("Set-Cookie", "JSESSIONID" + EQUAL_LETTER + UUID.randomUUID());
            return generateResourceResponseByFileName(INDEX_HTML, httpHeader);
        }

        return generateResourceResponse(INDEX_HTML);
    }
}
