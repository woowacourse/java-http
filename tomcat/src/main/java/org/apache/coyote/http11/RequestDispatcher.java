package org.apache.coyote.http11;

public class RequestDispatcher {

    private final HandlerMapping handlerMapping = new HandlerMapping();
    private final StaticResourceHandler staticResourceHandler =
        new StaticResourceHandler();

    public void dispatch(final HttpRequest request, final HttpResponse response)
        throws Exception {
        if (!handlerMapping.containsPath(request.path())) {
            staticResourceHandler.handle(request, response);
            return;
        }
        final Session session = request.getSession();
        if (session.isCreated()) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.id());
        }
        handlerMapping.getController(request)
            .service(request, response);

        if (response.hasForwardPath()) {
            staticResourceHandler.handle(request, response);
        }

    }

}
