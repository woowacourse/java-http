package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
            return;
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            doPost(request, response);
            return;
        }
        response.sendError(HttpStatus.BAD_REQUEST);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST);
    }
    
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendError(HttpStatus.BAD_REQUEST);
    }
}
