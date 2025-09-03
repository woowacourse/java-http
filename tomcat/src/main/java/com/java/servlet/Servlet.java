package com.java.servlet;

public interface Servlet {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request);
}
