package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface RequestHandler {

    HttpResponse handle(HttpRequest request) throws Exception;
}
