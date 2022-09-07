package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;

public interface RequestServlet {

    String EMPTY_BODY = "";

    ServletResponseEntity doGet(HttpRequest httpRequest, HttpHeader responseHeader);
    ServletResponseEntity doPost(HttpRequest httpRequest, HttpHeader responseHeader);
}
