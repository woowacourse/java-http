package org.apache.catalina.mvc;


import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public interface Controller {
    boolean matchesRequest(HttpRequest request);

    HttpResponse handleRequest(HttpRequest request);

    HttpResponse doGet(HttpRequest request);

    HttpResponse doPost(HttpRequest request);
}
