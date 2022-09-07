package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.request.HttpRequest;

public interface RequestServlet {

    String EMPTY_BODY = "";

    ServletResponseEntity doGet(HttpRequest httpRequest, HttpResponseHeader responseHeader);
    ServletResponseEntity doPost(HttpRequest httpRequest, HttpResponseHeader responseHeader);
}
