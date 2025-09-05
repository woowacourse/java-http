package org.apache.coyote.http11.service;

import org.apache.coyote.http11.parser.ContentParseResult;

import java.util.Map;

public class HelloService implements HttpService {

    private static final byte[] content = "Hello world!".getBytes();

    @Override
    public ContentParseResult doRequest(final Map<String, String> query) {
        return new ContentParseResult(content, "Content-Type: text/html;charset=utf-8 ");
    }
}
