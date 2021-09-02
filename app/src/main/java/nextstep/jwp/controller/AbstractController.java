package nextstep.jwp.controller;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
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
        final String uri = getUri(request);
        try {
            final StaticResource staticResource = staticResourceFinder.findStaticResource(uri + fileNameExtension);
            response.assignStatus(ResponseStatus.OK);
            response.addStaticResource(staticResource);
        } catch (NotFoundException e) {
            final StaticResource staticResource = staticResourceFinder.findStaticResource("/404.html");
            response.assignStatus(ResponseStatus.NOT_FOUND);
            response.addStaticResource(staticResource);
        }
    }

    private String getUri(HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        final String uri = requestLine.getUri();
        if ("/".equals(uri)) {
            return "/index";
        }
        return uri;
    }

    protected void assignRedirectToResponse(HttpResponse response, String locationHeader) {
        response.assignStatus(ResponseStatus.FOUND);
        response.assignLocationHeader(locationHeader);
    }
}

