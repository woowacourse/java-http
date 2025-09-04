package org.apache.controller;

import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;

public interface Controller {

    boolean isProcessableRequest(HttpRequestMessage request);

    void processRequest(HttpRequestMessage request, HttpResponseMessage response);
}
