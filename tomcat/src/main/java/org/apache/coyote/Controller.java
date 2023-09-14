package org.apache.coyote;

import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;

public interface Controller {

    boolean support(Request request);

    void service(Request request, Response response) throws Exception;
}
