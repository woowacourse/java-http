package org.apache.coyote.support;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Controller {
    Http11Response service(final Http11Request request);
}
