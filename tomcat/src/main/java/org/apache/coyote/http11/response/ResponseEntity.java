package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpExtensionType;
import org.apache.coyote.http11.common.HttpStatus;

public class ResponseEntity {

    private static final String JSESSION_ID = "JSESSIONID";

    private final HttpStatus httpStatus;
    private final String uri;
    private final HttpCookie httpCookie;

    public ResponseEntity(final HttpStatus httpStatus, final String uri) {
        this(httpStatus, uri, new HttpCookie());
    }

    public ResponseEntity(final HttpStatus httpStatus, final String uri, final HttpCookie httpCookie) {
        this.httpStatus = httpStatus;
        this.uri = uri;
        this.httpCookie = httpCookie;
    }

    public void setCookie(final String key, final String value) {
        httpCookie.put(key, value);
    }

    public void setJSessionId(final String id) {
        httpCookie.put(JSESSION_ID, id);
    }

    public HttpExtensionType getHttpExtensionType() {
        return HttpExtensionType.from(uri);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUri() {
        return uri;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
