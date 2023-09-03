package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import java.io.IOException;

public interface HttpDispatcher {
    Http11Response handle(final Http11Request request) throws IOException;
}
