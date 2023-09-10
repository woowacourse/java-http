package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.CharSet;
import org.apache.coyote.http11.resource.ContentType;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.resource.ResponseStatus;
import org.apache.coyote.http11.response.HttpResponse;

@RequestMapping(regex = ".*\\.(html|css|js)$")
public class StaticResourceController extends AbstractController {


    private final FileHandler fileHandler = new FileHandler();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        for (ContentType contentType : ContentType.values()) {
            if (request.getEndPoint().endsWith("." + contentType.getSymbol())) {
                response.setCharSet(CharSet.UTF_8);
                response.setContentType(contentType);
                response.setResponseStatus(ResponseStatus.OK);
                response.setResponseBody(fileHandler.readFromResourcePath(request.getFullUri()));
                response.flush();

                return;
            }
        }
    }
}
