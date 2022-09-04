package org.apache.coyote.http11.handler;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public interface RequestHandler {

    String handle(HttpRequest httpRequest);
}
