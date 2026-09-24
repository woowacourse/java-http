package org.apache.coyote.http11;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            doPost(request, response);
        } else if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
