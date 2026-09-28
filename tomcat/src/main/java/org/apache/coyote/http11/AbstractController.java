package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.sendError(HttpStatus.NOT_FOUND);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND);
    }
}
