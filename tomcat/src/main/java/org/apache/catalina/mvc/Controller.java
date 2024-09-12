package org.apache.catalina.mvc;


import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

public interface Controller {

    boolean isMatchesRequest(HttpRequest request);

    void handleRequest(HttpRequest request, HttpResponse response);

    void doGet(HttpRequest request, HttpResponse response);

    void doPost(HttpRequest request, HttpResponse response);
}
