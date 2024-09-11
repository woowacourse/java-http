package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public interface HttpRequestHandler {

    boolean supports(HttpServletRequest request);

    HttpServletResponse handle(HttpServletRequest request) throws IOException;
}
