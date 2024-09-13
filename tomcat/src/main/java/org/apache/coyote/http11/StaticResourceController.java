package org.apache.coyote.http11;

class StaticResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setContentType(ContentType.determineContentType(request.getPath()));
        response.setResponseBody(readResource(request.getPath()));
    }
}
