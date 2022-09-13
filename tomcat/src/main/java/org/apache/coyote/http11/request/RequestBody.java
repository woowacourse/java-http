package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestBody {

    private final String contents;

    public RequestBody(final String contents) {
        this.contents = contents;
    }

    public Map<String, String> parseApplicationFormData() {
        return QueryParser.parse(contents, "올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
    }
}
