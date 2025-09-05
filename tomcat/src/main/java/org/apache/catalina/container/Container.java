package org.apache.catalina.container;

import com.java.http.HttpRequest;
import com.java.http.HttpResponse;

public interface Container {

    HttpResponse service(HttpRequest request);
}
