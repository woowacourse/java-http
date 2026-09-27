package org.apache.coyote.http11;

public class RootController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        ok(response, ResourceLoader.CONTENT_TYPE_HTML, "Hello world!");
    }
}
