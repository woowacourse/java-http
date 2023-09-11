package org.apache.coyote;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Mapper {

    void addController(String path, Controller controller);

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
