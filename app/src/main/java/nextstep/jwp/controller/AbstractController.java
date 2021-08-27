package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResource;
import nextstep.jwp.staticresource.StaticResourceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);
    protected final StaticResourceFinder staticResourceFinder;

    protected AbstractController(StaticResourceFinder staticResourceFinder) {
        this.staticResourceFinder = staticResourceFinder;
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void assignStaticResourceByUriToResponse(HttpRequest request, HttpResponse response, String fileNameExtension) {
        final RequestLine requestLine = request.getRequestLine();
        final String uri = requestLine.getUri();
        final StaticResource staticResource = staticResourceFinder.findStaticResource(uri + fileNameExtension);
        response.assignStatusCode(200);
        response.addStaticResource(staticResource);
    }

    protected void assignRedirectToResponse(HttpResponse response, String locationHeader) {
        response.assignStatusCode(302);
        response.assignLocationHeader(locationHeader);
    }
}

