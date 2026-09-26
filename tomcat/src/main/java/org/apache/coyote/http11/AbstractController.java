package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod("POST")) {
            doPost(request, response);
        } else if (request.isMethod("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.sendMethodNotAllowed();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendMethodNotAllowed();
    }
}
