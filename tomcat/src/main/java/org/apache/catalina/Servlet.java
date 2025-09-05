package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {
    
    void init();
    
    void service(HttpRequest request, HttpResponse response);
    
    void destroy();
}
