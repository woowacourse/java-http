package org.apache.coyote;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface Servlet {

    boolean support(HttpRequest httpRequest);

    HttpResponse doService(HttpRequest httpRequest);
}
