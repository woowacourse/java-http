package org.apache.catalina;

import com.techcourse.http.HttpRequest;

public interface ControllerResolver {

    Controller getController(HttpRequest request) throws Exception;
}
