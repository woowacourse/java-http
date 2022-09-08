package org.apache.catalina.servlet;

import java.util.List;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.request.HttpRequest;

public interface Controller {

    void service(HttpRequest request, HttpResponse response);

    List<String> getMappedPaths();
}
