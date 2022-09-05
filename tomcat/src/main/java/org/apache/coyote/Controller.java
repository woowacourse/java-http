package org.apache.coyote;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public interface Controller {
    boolean isProcessable(HttpRequest request);

    String process(Map<String, String> params);
}
