package com.techcourse.controller;

import java.io.File;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.resource.ResourceParser;

public class ResourceController implements Controller {

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        File requestFile = ResourceParser.getRequestFile(req);
        resp.setResponse("200 OK", requestFile);
    }
}
