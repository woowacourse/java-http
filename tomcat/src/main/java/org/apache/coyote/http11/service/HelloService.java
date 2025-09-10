package org.apache.coyote.http11.service;

import org.apache.coyote.http11.HttpRequests;
import org.apache.coyote.http11.parser.HttpResponse;

public class HelloService implements HttpService {

    private static final byte[] content = "Hello world!".getBytes();

    @Override
    public void doGet(HttpRequests httpRequests, HttpResponse httpResponse) {
        httpResponse.setContent(content);
        httpResponse.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doPost(HttpRequests httpRequests, HttpResponse httpResponse) {
        throw new IllegalArgumentException("제공되지 않는 기능입니다");
    }

    @Override
    public void doUpdate(HttpRequests httpRequests, HttpResponse httpResponse) {
        throw new IllegalArgumentException("제공되지 않는 기능입니다.");
    }

    @Override
    public void doDelete(HttpRequests httpRequests, HttpResponse httpResponse) {
        throw new IllegalArgumentException("제공되지 않는 기능입니다.");
    }
}
