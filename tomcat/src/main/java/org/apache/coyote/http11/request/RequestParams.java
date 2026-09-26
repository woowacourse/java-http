package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.FormUrlEncoded;

public class RequestParams {

    private final Map<String, String> values;

    public RequestParams(final String queryString) {
        this.values = FormUrlEncoded.parse(queryString);
    }

    public String get(final String name) {
        return values.get(name);
    }
}
