package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Adapter {
    void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception;
}
