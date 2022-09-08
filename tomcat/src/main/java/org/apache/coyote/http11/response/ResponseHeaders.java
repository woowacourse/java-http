package org.apache.coyote.http11.response;

import java.util.List;

public class ResponseHeaders {

    private List<String> values;

    public ResponseHeaders(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}
