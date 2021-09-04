package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.HttpSession;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;

public abstract class AbstractController implements Controller {

    protected static final String HTML_EXTENSION = ".html";
    protected static final String HOMEPAGE = "/index.html";

    private final String resource;

    protected AbstractController(String resource) {
        this.resource = resource;
    }

    @Override
    public final String getResource() {
        return resource;
    }

    @Override
    public final void service(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
        // BAD REQUEST? NOT FOUND?
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected final void okWithResource(HttpResponse response) {
        final View view = new View(getResource() + HTML_EXTENSION);
        response.setStatus(HttpStatus.OK);
        response.setBody(view);
    }

    protected final void redirect(HttpResponse response, String location) {
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", location);
    }

    protected final void redirectWithSessionCookie(HttpResponse response, String location, HttpSession session) {
        response.setHeader("Set-Cookie", session.asCookieString());
        redirect(response, location);
    }

    protected final void unauthorized(HttpResponse response) {
        final View view = new View("/401");
        response.setStatus(HttpStatus.UNAUTHORIZED);
        response.setBody(view);
    }
}
