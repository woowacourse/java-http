package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.utils.PairConverter;

public class HttpBody {

    private final Map<String, String> body;

    public HttpBody(final String body) {
        this.body = PairConverter.toMap(body, "&", "=");
    }
}
