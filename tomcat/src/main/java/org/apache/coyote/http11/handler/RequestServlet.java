package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpRequest;

public interface RequestServlet {

    ServletResponseEntity doGet(HttpRequest httpRequest);
    ServletResponseEntity doPost(HttpRequest httpRequest);
}
