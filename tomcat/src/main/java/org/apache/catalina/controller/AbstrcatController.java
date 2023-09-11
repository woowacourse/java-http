package org.apache.catalina.controller;

import org.apache.catalina.exception.CustomBadRequestException;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.exception.MissingRequestBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstrcatController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstrcatController.class);
    private static final String GET = "GET";
    private static final String POST = "POST";

    private final String mappedUrl;

    protected AbstrcatController(final String mappedUrl) {
        this.mappedUrl = mappedUrl;
    }

    @Override
    public boolean isMappedController(HttpRequest request) {
        return mappedUrl.equals(request.getPath());
    }

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        try {
            if (GET.equals(request.getMethod())) {
                doGet(request, response);
                return;
            }
            if (POST.equals(request.getMethod())) {
                doPost(request, response);
                return;
            }
            response.methodNotAllowed();
        } catch (MissingRequestBody | CustomBadRequestException e) {
            log.warn(e.getMessage());
            response.badRequest(e.getMessage());
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
