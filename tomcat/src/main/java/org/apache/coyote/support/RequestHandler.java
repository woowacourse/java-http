package org.apache.coyote.support;

import java.util.Map;
import org.apache.coyote.http11.http.HttpRequest;

public interface RequestHandler {

    Map<String, String> handle(HttpRequest httpRequest);
}
