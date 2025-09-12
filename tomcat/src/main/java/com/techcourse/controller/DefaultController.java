package com.techcourse.controller;

import java.io.FileNotFoundException;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.loader.ResourceLoader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.utils.UriUtils;

public class DefaultController extends AbstractController {

    private static final String DEFAULT_PATH = "/index.html";

    public DefaultController(final ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        handleError(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final byte[] resource = resourceLoader.getResourceAsBytes(DEFAULT_PATH);
            response.ok(resource, UriUtils.getMimeType(DEFAULT_PATH));
        } catch (FileNotFoundException e) {
            handleError(response, HttpStatus.NOT_FOUND);
        }
    }
}
