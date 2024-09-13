package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaderName;

public record Http11ResponseHeader(HttpHeaderName name, String value) {
}
