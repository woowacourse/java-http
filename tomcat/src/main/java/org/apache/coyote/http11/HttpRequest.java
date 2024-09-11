package org.apache.coyote.http11;

public record HttpRequest(RequestLine requestLine, HttpHeader httpHeader, RequestBody requestBody) {

}
