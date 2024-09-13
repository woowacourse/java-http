package org.apache.catalina.controller;

import com.techcourse.exception.ApplicationException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws ApplicationException;
}
