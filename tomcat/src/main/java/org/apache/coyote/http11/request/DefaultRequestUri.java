package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultRequestUri extends Request {

    public DefaultRequestUri(String method, String uri, Map<String, String> header, Map<String, String> body) {
        super(method, uri, header, body);
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public String getResponseBody() {
        return "Hello world!";
    }

    @Override
    public Optional<Map<String, String>> getQueries() {
        return Optional.empty();
    }

    @Override
    public String getApi() {
        return "/";
    }
}
