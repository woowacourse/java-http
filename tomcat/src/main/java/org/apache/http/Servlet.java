package org.apache.http;

public interface Servlet {

    void init();

    void service(Request request, Response response);
}
