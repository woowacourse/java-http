package com.java.servlet;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

public interface Servlet {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
