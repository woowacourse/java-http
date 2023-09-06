package org.apache.coyote;

import java.io.IOException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;

public interface Handler {

    boolean supports(final Request request, final String contextPath);

    Response service(final Request request, final String staticResourcePath) throws IOException;
}
