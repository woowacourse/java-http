package org.apache.coyote.http11.common.header;

import java.util.Map;

public class ResponseHeaders extends Headers {

    public ResponseHeaders() {
    }

    public ResponseHeaders(final Map<String, String> headers) {
        super(headers);
    }

    @Override
    public boolean isType(final String headerName) {
        return HeaderName.isResponseHeaders(headerName);
    }

    public void addLocation(final String location) {
        add(HeaderName.LOCATION, location);
    }

    public void addSetCookie(final String setCookie) {
        add(HeaderName.SET_COOKIE, setCookie);
    }

    public String getLocation() {
        return find(HeaderName.LOCATION);
    }

}
