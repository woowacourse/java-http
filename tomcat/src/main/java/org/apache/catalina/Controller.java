package org.apache.catalina;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
