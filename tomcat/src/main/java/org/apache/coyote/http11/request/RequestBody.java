package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;

public class RequestBody {

    private static final String EMPTY_PAYLOAD = "";

    private final Map<String, String> payloads;

    public RequestBody(String payloads) {
        this.payloads = RequestFormatter.toBodyValueFormat(payloads);
    }

    public static RequestBody empty() {
        return new RequestBody(EMPTY_PAYLOAD);
    }

    public Map<String, String> getContents() {
        return Collections.unmodifiableMap(payloads);
    }
}
