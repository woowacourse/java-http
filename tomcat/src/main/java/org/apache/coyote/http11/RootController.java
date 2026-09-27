package org.apache.coyote.http11;

import java.io.IOException;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.send("text/html;charset=utf-8", "Hello world!");
    }
}
