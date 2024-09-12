package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public interface Servlet {

    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
