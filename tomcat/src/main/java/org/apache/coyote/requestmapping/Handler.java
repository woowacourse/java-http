package org.apache.coyote.requestmapping;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface Handler {
    HttpResponse handle(HttpRequest httpRequest);
}
