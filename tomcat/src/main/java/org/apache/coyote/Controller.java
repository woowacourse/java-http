package org.apache.coyote;

import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public interface Controller {

    Response service(Request request) throws Exception;

    boolean canHandle(Request request);
}
