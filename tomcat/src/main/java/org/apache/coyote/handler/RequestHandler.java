package org.apache.coyote.handler;

import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;

public interface RequestHandler {
    String handle(String method, String path, Map<String, String> queryParams, HttpCookie cookie);
}
