package nextstep.jwp.web.controller;

import nextstep.jwp.web.exception.BadRequestException;
import nextstep.jwp.web.exception.NotFoundException;
import nextstep.jwp.web.network.HttpSession;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;

public abstract class AbstractController implements Controller {

    protected static final String HTML_EXTENSION = ".html";
    protected static final String HOME_PAGE = "/index" + HTML_EXTENSION;
    protected static final String BAD_REQUEST_PAGE = "/400";
    protected static final String UNAUTHORIZED_PAGE = "/401";
    protected static final String NOT_FOUND_PAGE = "/404";
    protected static final String INTERNAL_SERVER_ERROR_PAGE = "/500";

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
        try {
            if (request.isGet()) {
                doGet(request, response);
            }
            if (request.isPost()) {
                doPost(request, response);
            }
        } catch (BadRequestException exception) {
            controlException(response, HttpStatus.BAD_REQUEST, BAD_REQUEST_PAGE);
        } catch (NotFoundException exception) {
            controlException(response, HttpStatus.NOT_FOUND, NOT_FOUND_PAGE);
        } catch (Exception exception) {
            controlException(response, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_PAGE);
        }
    }

    private void controlException(HttpResponse response, HttpStatus status, String viewName) {
        response.setStatus(status);
        response.setBody(new View(viewName));
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
        final View view = new View(UNAUTHORIZED_PAGE);
        response.setStatus(HttpStatus.UNAUTHORIZED);
        response.setBody(view);
    }
}
