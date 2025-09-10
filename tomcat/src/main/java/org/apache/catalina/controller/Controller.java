package org.apache.catalina.controller;

import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;

public interface Controller {

    void service(ServletRequest request, ServletResponse response);
}
