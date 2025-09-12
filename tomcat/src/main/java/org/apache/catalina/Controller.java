package org.apache.catalina;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public interface Controller {

    void service(final Request request, final Response response) throws Exception;
}
