package org.apache.catalina.servlet;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
