package org.apache.catalina.servlet.adapter;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {
     void service(HttpRequest request, HttpResponse response);
}
