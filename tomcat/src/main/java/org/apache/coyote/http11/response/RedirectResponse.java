package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaders;

public class RedirectResponse extends HttpResponse {

    private RedirectResponse(final ContentType contentType,
                             final HttpStatus status,
                             final HttpHeaders unnecessaryHeaders,
                             final String responseBody) {
        super(contentType, status, unnecessaryHeaders, responseBody);
    }

    public static RedirectResponse of(final String redirectUri) {
        final List<HttpHeader> header = new ArrayList<>();
        header.add(new HttpHeader(HttpHeaders.LOCATION, redirectUri));
        return new RedirectResponse(
                ContentType.TEXT_HTML,
                HttpStatus.FOUND,
                new HttpHeaders(header),
                ""
        );
    }
}
