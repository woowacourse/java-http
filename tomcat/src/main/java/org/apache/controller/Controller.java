package org.apache.controller;

import org.apache.http.HttpRequestMessage;

public interface Controller {

    boolean isProcessableRequest(HttpRequestMessage request);

    String processRequest(HttpRequestMessage request);
}
