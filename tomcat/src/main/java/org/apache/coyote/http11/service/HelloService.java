package org.apache.coyote.http11.service;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.RequestResult;

import java.util.Map;

public class HelloService implements HttpService {

    private static final byte[] content = "Hello world!".getBytes();

    @Override
    public RequestResult doGet(final Map<String, String> query, HttpCookies cookies, Session session) {
        return new RequestResult(content, "Content-Type: text/html;charset=utf-8 ");
    }

    @Override
    public RequestResult doPost(Map<String, String> query, HttpCookies cookies, Session session) {
        throw new IllegalArgumentException("제공되지 않는 기능입니다");
    }
}
