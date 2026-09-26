package org.apache.coyote.http11;

public class StaticFileController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resourcePath = request.getPath();
        String responseBody = ResourceLoader.loadResponseBody(resourcePath);
        String contentType = ResourceLoader.findContentType(resourcePath);

        ok(response, contentType, responseBody);
    }
}
