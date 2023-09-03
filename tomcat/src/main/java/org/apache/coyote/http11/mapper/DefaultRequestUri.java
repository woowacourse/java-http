package org.apache.coyote.http11.mapper;

import java.util.Map;
import java.util.Optional;

public class DefaultRequestUri extends Mapper{

    protected DefaultRequestUri(String method, String uri, Map<String, String> header) {
        super(method, uri, header);
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
}
