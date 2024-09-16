package jakarta.controller;

import jakarta.http.ContentType;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpStatus;

public class StaticResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        response.setContentType(ContentType.determineContentType(request.getPath()));
        response.setResponseBody(readResource(request.getPath()));
    }
}
