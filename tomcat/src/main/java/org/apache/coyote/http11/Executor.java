package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Executor {
    HttpResponse execute(HttpRequest req);

    boolean isMatch(HttpRequest req);
}
