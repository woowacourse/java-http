package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;

public record HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {

}
