package org.apache.catalina.controller;

import java.io.FileNotFoundException;
import org.apache.catalina.loader.ResourceLoader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.utils.UriUtils;

public class StaticResourceController extends AbstractController {

    public StaticResourceController(final ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        handleError(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final String path = "/" + request.getPath();
            final byte[] resource = resourceLoader.getResourceAsBytes(path);
            response.ok(resource, UriUtils.getMimeType(path));
        } catch (FileNotFoundException e) {
            handleError(response, HttpStatus.NOT_FOUND);
        }
    }
}
