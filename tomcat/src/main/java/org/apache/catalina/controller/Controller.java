package org.apache.catalina.controller;

import org.apache.catalina.Request;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

    HttpResponse handle(Request request) throws Exception;
}
