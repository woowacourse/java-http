package org.apache.controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface Controller {

    boolean isProcessableRequest(HttpRequest request);

    void processRequest(HttpRequest request, HttpResponse response);
}
