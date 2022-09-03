package org.apache.coyote.http11.request;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Resource;

import nextstep.jwp.controller.UrlMapper;

public class RequestHandler {

    public String handle(final RequestHeader request) {
        final Uri uri = Uri.parse(request.getUri());

        if (UrlMapper.exist(uri.getPath())) {
            final HttpResponse response = UrlMapper.run(uri);
            return response.asFormat();
        }
        return resource(uri.getPath());
    }

    private String resource(final String filePath) {
        return new HttpResponse()
                .status(HttpStatus.OK)
                .body(new Resource(filePath))
                .asFormat();
    }
}
