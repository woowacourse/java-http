package org.apache.coyote.http11;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
