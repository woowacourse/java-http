package org.apache.controller;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface Controller {

    HttpResponse service(HttpRequest request);
}
