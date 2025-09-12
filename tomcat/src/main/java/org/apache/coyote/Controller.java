package org.apache.coyote;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public interface Controller {
    void service(Request request, Response response) throws Exception;
}
