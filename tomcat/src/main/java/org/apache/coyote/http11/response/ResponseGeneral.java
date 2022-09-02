package org.apache.coyote.http11.response;

import org.apache.coyote.http11.message.HttpVersion;

public class ResponseGeneral implements Response {

    private final HttpVersion version;
    private final HttpStatus status;

    public ResponseGeneral(HttpVersion version, HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    @Override
    public String getAsString() {
        return version.getVersion() + " " + status.getAsString();
    }
}
