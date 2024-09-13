package org.apache.coyote.fixture;

import org.apache.coyote.request.requestLine.RequestLine;

public class RequestLineFixture {

    public static RequestLine NOT_EXIST_METHOD_REQUESTLINE = new RequestLine("PUT / HTTP/1.1");
    public static RequestLine DEFAULT_GET = new RequestLine("GET / HTTP/1.1");
    public static RequestLine DEFAULT_POST = new RequestLine("POST / HTTP/1.1");
    public static RequestLine REGISTER_GET = new RequestLine("GET /register HTTP/1.1");
    public static RequestLine REGISTER_POST = new RequestLine("POST /register HTTP/1.1");
    public static RequestLine LOGIN_GET = new RequestLine("GET /login HTTP/1.1");
    public static RequestLine LOGIN_POST = new RequestLine("POST /login HTTP/1.1");
    public static RequestLine MAIN_RESOURCE_POST = new RequestLine("POST /index.html HTTP/1.1");
    public static RequestLine MAIN_RESOURCE_GET = new RequestLine("GET /index.html HTTP/1.1");
}
