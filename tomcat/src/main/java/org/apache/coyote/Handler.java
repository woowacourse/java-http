package org.apache.coyote;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public interface Handler {

    Http11Response resolve(Http11Request request);
}
