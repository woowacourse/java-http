package org.apache.controller;

import org.apache.http.HttpResponse;
import org.apache.http.request.HttpRequest;

public interface Controller {

    boolean isProcessableRequest(HttpRequest request);

    void processRequest(HttpRequest request, HttpResponse response);
}
