package org.apache.coyote.http11;

public class StaticResourceController extends AbstractController{

    private final StaticResourceHandler staticHandler = new StaticResourceHandler();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        staticHandler.serve(request, response);
    }
}
