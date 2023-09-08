package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.exception.ResourceLoadingException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            final HttpResponse newResponse =
                StaticResourceResponseSupplier.getWithExtensionContentType(request.getPath());
            response.update(newResponse);
        } catch (IOException e) {
            throw new ResourceLoadingException(request.getPath());
        }
    }
}
