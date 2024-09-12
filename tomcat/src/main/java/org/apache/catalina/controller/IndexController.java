package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.util.ResourceFile;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class IndexController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("POST는 지원하지 않습니다.");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        ResourceFile resourceFile = ResourceReader.readResource("/index.html");
        response.setBody(resourceFile);
        response.setStatus(HttpStatus.OK);
    }
}
