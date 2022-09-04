package org.apache.coyote.http11.handler;

import java.util.Map;

public interface RequestHandler {

    String handle(Map<String, String> queryParams);
}
