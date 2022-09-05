package org.apache.coyote.http11.request.headers;

import org.apache.coyote.http11.header.HttpHeader;

public interface RequestHeader extends HttpHeader {
    String getField();

    String getValue();
}
