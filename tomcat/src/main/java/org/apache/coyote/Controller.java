package org.apache.coyote;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public interface Controller {

    void service(final Request request, final Response response) throws Exception;
}
