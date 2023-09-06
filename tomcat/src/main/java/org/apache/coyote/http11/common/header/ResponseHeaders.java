package org.apache.coyote.http11.common.header;

import static org.apache.coyote.http11.common.header.HeaderName.LOCATION;
import static org.apache.coyote.http11.common.header.HeaderName.SET_COOKIE;

import java.util.Map;

public class ResponseHeaders extends Headers {

    public ResponseHeaders() {
    }

    public ResponseHeaders(final Map<HeaderName, String> headers) {
        super(headers);
    }

    @Override
    public boolean isType(final HeaderName headerName) {
        return HeaderName.isResponseHeaders(headerName);
    }

    public void addLocation(final String location) {
        add(LOCATION, location);
    }

    public void addSetCookie(final String setCookie) {
        add(SET_COOKIE, setCookie);
    }

    public String getLocation() {
        return find(LOCATION);
    }

}
