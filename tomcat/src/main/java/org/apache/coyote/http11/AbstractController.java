package org.apache.coyote.http11;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case "GET" -> doGet(request, response);
            case "POST" -> doPost(request, response);
            default -> response.setStatus(405, "Method Not Allowed");
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setStatus(405, "Method Not Allowed");
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setStatus(405, "Method Not Allowed");
    }
}
