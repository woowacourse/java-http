package org.apache.coyote;

import org.apache.coyote.http11.common.HttpRequest;
import org.apache.coyote.http11.common.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
