package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
            return;
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // NOOP
    }

    protected void ok(HttpResponse response, String contentType, String responseBody) {
        response.setStatus("200", "OK");
        response.setContentType(contentType);
        response.setResponseBody(responseBody);
    }

    protected void found(HttpResponse response, String path) {
        response.setStatus("302", "Found");
        response.setContentType(null);
        response.setResponseBody("");
        response.setLocation(path);
    }

    protected void unsupportedMediaType(HttpResponse response) {
        response.setStatus("415", "Unsupported Media Type");
        response.setContentType(null);
        response.setResponseBody("");
        response.setLocation(null);
    }

    protected void badRequest(HttpResponse response) {
        response.setStatus("400", "Bad Request");
        response.setContentType(null);
        response.setResponseBody("");
        response.setLocation(null);
    }
}
