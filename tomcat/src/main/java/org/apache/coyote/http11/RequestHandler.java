package org.apache.coyote.http11;

import java.util.Map;

public interface RequestHandler {
    String handle(Map<String, String> paramsMap);
}
