package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Mapper {
    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
