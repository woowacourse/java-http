package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.header.HttpHeaderType.COOKIE;

import java.util.UUID;
import org.apache.coyote.http11.header.HttpCookie;
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

        if (!headers.contains(COOKIE.getValue())) {
            return generateResourceResponseWithCookie("JSESSIONID" + EQUAL_LETTER + UUID.randomUUID());
        }

        final HttpHeader httpHeader = headers.get(COOKIE.getValue());
        if (!httpHeader.getValues().contains("JSESSIONID")) {
            return generateResourceResponseWithCookie("JSESSIONID" + EQUAL_LETTER + UUID.randomUUID());
        }

        return generateResourceResponse(INDEX_HTML);
    }

    private HttpResponse generateResourceResponseWithCookie(final String value) {
        final HttpHeader httpHeader = HttpCookie.generateResponseHeader(value);
        return generateResourceResponseByFileName(INDEX_HTML, httpHeader);
    }
}
