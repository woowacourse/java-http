package org.apache.coyote.http.response;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpVersion;

public class HttpResponse {

    private final HttpVersion version;
    private final HttpStatus httpStatus;
    private final String responseBody;
    private final HttpHeader httpHeader;

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String responseBody) {
        this(HttpVersion.HTTP11, httpStatus, responseBody,
                HttpHeader.init(contentType, responseBody));
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String responseBody,
                        final String redirectUrl) {
        this(HttpVersion.HTTP11, httpStatus, responseBody,
                HttpHeader.init(contentType, responseBody, redirectUrl));
    }

    private HttpResponse(final HttpVersion version, final HttpStatus httpStatus, final String responseBody,
                         final HttpHeader httpHeader) {
        this.version = version;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
        this.httpHeader = httpHeader;
    }

    public String convertTemplate() {
        return String.join("\r\n",
                version.getVersion() + " " + httpStatus.getHttpStatusMessage(),
                printHeader() + "\n",
                this.responseBody);
    }

    public String printHeader() {
        return httpHeader.print();
    }

}
